package com.sample.system.reporting.service.messaging.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitMqConfigurationTest {

    private final RabbitMqConfiguration configuration = new RabbitMqConfiguration();

    @Test
    void declaresTopicExchangeAndDurableQueue() {
        TopicExchange exchange = configuration.integrationEventsExchange("banking.integration.events");
        Queue queue = configuration.partyQueue("reporting.party.events", "banking.integration.dlx");

        assertEquals("banking.integration.events", exchange.getName());
        assertTrue(exchange.isDurable());
        assertEquals("reporting.party.events", queue.getName());
        assertTrue(queue.isDurable());
        assertEquals("banking.integration.dlx", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("reporting.party.events.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void bindsPartyQueueToProducerRoutingKey() {
        TopicExchange exchange = configuration.integrationEventsExchange("banking.integration.events");
        Queue queue = configuration.partyQueue("reporting.party.events", "banking.integration.dlx");

        Binding binding = configuration.partyBinding(queue, exchange, "party.events");

        assertEquals("party.events", binding.getRoutingKey());
    }

    @Test
    void declaresAndBindsDurablePartyDeadLetterQueue() {
        DirectExchange exchange = configuration.deadLetterExchange("banking.integration.dlx");
        Queue queue = configuration.partyDeadLetterQueue("reporting.party.events");

        Binding binding = configuration.partyDeadLetterBinding(queue, exchange);

        assertEquals("reporting.party.events.dlq", queue.getName());
        assertTrue(queue.isDurable());
        assertEquals("banking.integration.dlx", binding.getExchange());
        assertEquals(queue.getName(), binding.getRoutingKey());
    }

    @Test
    void declaresRetryQueuesThatDeadLetterBackToTheOriginalQueue() {
        var declarables = configuration.retryQueues(
                "q.party", "q.card", "q.loan", "q.accounting", "q.product", 15000L);

        var queues = declarables.getDeclarablesByType(Queue.class);
        assertEquals(5, queues.size());
        Queue party = queues.stream().filter(q -> q.getName().equals("q.party.retry")).findFirst().orElseThrow();
        assertEquals(15000L, party.getArguments().get("x-message-ttl"));
        assertEquals("", party.getArguments().get("x-dead-letter-exchange"));
        assertEquals("q.party", party.getArguments().get("x-dead-letter-routing-key"));
    }
}
