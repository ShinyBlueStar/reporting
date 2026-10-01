package com.sample.system.reporting.service.messaging.consumer;

import com.sample.system.reporting.service.messaging.processor.ReportingEventProcessor;
import com.rabbitmq.client.Channel;
import com.sample.system.reporting.service.messaging.config.RabbitMqConfiguration;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ReportingEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReportingEventConsumer.class);

    static final String RETRY_HEADER = "x-retry-count";

    private final ReportingEventProcessor reportingEventProcessor;
    private final RabbitTemplate rabbitTemplate;
    private final int maxAttempts;

    public ReportingEventConsumer(ReportingEventProcessor reportingEventProcessor,
                                  RabbitTemplate rabbitTemplate,
                                  @Value("${reporting.rabbitmq.retry.max-attempts:3}") int maxAttempts) {
        this.reportingEventProcessor = reportingEventProcessor;
        this.rabbitTemplate = rabbitTemplate;
        this.maxAttempts = maxAttempts;
    }

    @RabbitListener(
            queues = "${reporting.rabbitmq.queues.party:reporting.party.events}",
            containerFactory = "reportingRabbitListenerContainerFactory"
    )
    public void consumePartyEvent(Message message, Channel channel) throws IOException {
        consume(message, channel);
    }

    @RabbitListener(
            queues = "${reporting.rabbitmq.queues.card:reporting.card.events}",
            containerFactory = "reportingRabbitListenerContainerFactory"
    )
    public void consumeCardEvent(Message message, Channel channel) throws IOException {
        consume(message, channel);
    }

    @RabbitListener(
            queues = "${reporting.rabbitmq.queues.loan:reporting.loan.events}",
            containerFactory = "reportingRabbitListenerContainerFactory"
    )
    public void consumeLoanEvent(Message message, Channel channel) throws IOException {
        consume(message, channel);
    }

    @RabbitListener(
            queues = "${reporting.rabbitmq.queues.accounting:reporting.accounting.events}",
            containerFactory = "reportingRabbitListenerContainerFactory"
    )
    public void consumeAccountingEvent(Message message, Channel channel) throws IOException {
        consume(message, channel);
    }

    @RabbitListener(
            queues = "${reporting.rabbitmq.queues.product:reporting.product.events}",
            containerFactory = "reportingRabbitListenerContainerFactory"
    )
    public void consumeProductEvent(Message message, Channel channel) throws IOException {
        consume(message, channel);
    }

    private void consume(Message message, Channel channel) throws IOException {
        MessageProperties properties = message.getMessageProperties();
        long deliveryTag = properties.getDeliveryTag();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            reportingEventProcessor.process(body);
            channel.basicAck(deliveryTag, false);
        } catch (RuntimeException ex) {
            handleFailure(message, channel, deliveryTag, ex);
        }
    }

    /**
     * Malformed events can never succeed and go straight to the DLQ. Other failures (database down, lock
     * timeouts, ...) are retried through the delay queue up to {@code maxAttempts}, then parked in the DLQ.
     */
    private void handleFailure(Message message, Channel channel, long deliveryTag, RuntimeException ex) throws IOException {
        MessageProperties properties = message.getMessageProperties();
        String queue = properties.getConsumerQueue();
        int attempt = retryCount(properties);
        boolean retryable = !(ex instanceof IllegalArgumentException) && queue != null && attempt < maxAttempts;
        if (!retryable) {
            channel.basicNack(deliveryTag, false, false);
            log.error("Reporting event processing failed permanently; message moved to the dead-letter queue. "
                    + "queue={}, attempts={}, messageId={}", queue, attempt + 1, properties.getMessageId(), ex);
            return;
        }
        properties.setHeader(RETRY_HEADER, attempt + 1);
        // Publish first, ack second: if publishing fails the message is requeued by the broker, never lost.
        rabbitTemplate.send("", RabbitMqConfiguration.retryQueueName(queue), message);
        channel.basicAck(deliveryTag, false);
        log.warn("Reporting event processing failed; scheduled retry {}/{}. queue={}, messageId={}, cause={}",
                attempt + 1, maxAttempts, queue, properties.getMessageId(), ex.toString());
    }

    private int retryCount(MessageProperties properties) {
        Object header = properties.getHeader(RETRY_HEADER);
        return header instanceof Number n ? n.intValue() : 0;
    }
}
