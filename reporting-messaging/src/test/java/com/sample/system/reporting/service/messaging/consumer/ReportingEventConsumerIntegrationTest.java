package com.sample.system.reporting.service.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.messaging.processor.ReportingEventProcessor;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReportingEventConsumerIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final ReportingEventProcessor reportingEventProcessor = mock(ReportingEventProcessor.class);
    private final Channel channel = mock(Channel.class);
    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private final ReportingEventConsumer consumer = new ReportingEventConsumer(reportingEventProcessor, rabbitTemplate, 3);

    @Test
    void consumesAndAcknowledgesValidEvent() throws Exception {
        EventEnvelope envelope = envelope();
        Message message = message(objectMapper.writeValueAsString(envelope), 1L);

        consumer.consumePartyEvent(message, channel);

        verify(reportingEventProcessor).process(anyString());
        verify(channel).basicAck(1L, false);
    }

    @Test
    void nacksAndDoesNotAcknowledgeFailedEvent() throws Exception {
        Message message = message("not-json", 2L);
        doThrow(new IllegalArgumentException("Invalid reporting event envelope"))
                .when(reportingEventProcessor).process("not-json");

        consumer.consumePartyEvent(message, channel);

        verify(channel).basicNack(2L, false, false);
        verify(channel, never()).basicAck(2L, false);
    }

    @Test
    void transientFailureIsRetriedThroughDelayQueueAndAcked() throws Exception {
        Message message = message("{}", 3L);
        doThrow(new IllegalStateException("db down")).when(reportingEventProcessor).process("{}");

        consumer.consumePartyEvent(message, channel);

        verify(rabbitTemplate).send(eq(""), eq("reporting.party.events.retry"), same(message));
        assertEquals(Integer.valueOf(1), message.getMessageProperties().<Integer>getHeader("x-retry-count"));
        verify(channel).basicAck(3L, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void failureAfterMaxAttemptsGoesToDeadLetterQueue() throws Exception {
        Message message = message("{}", 4L);
        message.getMessageProperties().setHeader("x-retry-count", 3);
        doThrow(new IllegalStateException("db down")).when(reportingEventProcessor).process("{}");

        consumer.consumePartyEvent(message, channel);

        verify(channel).basicNack(4L, false, false);
        verify(rabbitTemplate, never()).send(anyString(), anyString(), any(Message.class));
    }

    private Message message(String body, long deliveryTag) {
        MessageProperties properties = new MessageProperties();
        properties.setConsumerQueue("reporting.party.events");
        properties.setDeliveryTag(deliveryTag);
        return new Message(body.getBytes(StandardCharsets.UTF_8), properties);
    }

    private EventEnvelope envelope() {
        return new EventEnvelope(
                "event-1",
                "UPSERT",
                "PARTY",
                "party-1",
                1L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "party-service",
                "correlation-1",
                Map.of("nationalCode", "1234567890")
        );
    }
}
