package com.sample.system.reporting.service.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitMqConfiguration {

    @Bean
    public TopicExchange integrationEventsExchange(
            @Value("${reporting.rabbitmq.exchange:banking.integration.events}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange(
            @Value("${reporting.rabbitmq.dead-letter-exchange:banking.integration.dlx}") String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue partyQueue(
            @Value("${reporting.rabbitmq.queues.party:reporting.party.events}") String queueName,
            @Value("${reporting.rabbitmq.dead-letter-exchange:banking.integration.dlx}") String dlxName) {
        return deadLetterQueue(queueName, dlxName);
    }

    @Bean
    public Queue cardQueue(
            @Value("${reporting.rabbitmq.queues.card:reporting.card.events}") String queueName,
            @Value("${reporting.rabbitmq.dead-letter-exchange:banking.integration.dlx}") String dlxName) {
        return deadLetterQueue(queueName, dlxName);
    }

    @Bean
    public Queue loanQueue(
            @Value("${reporting.rabbitmq.queues.loan:reporting.loan.events}") String queueName,
            @Value("${reporting.rabbitmq.dead-letter-exchange:banking.integration.dlx}") String dlxName) {
        return deadLetterQueue(queueName, dlxName);
    }

    @Bean
    public Queue accountingQueue(
            @Value("${reporting.rabbitmq.queues.accounting:reporting.accounting.events}") String queueName,
            @Value("${reporting.rabbitmq.dead-letter-exchange:banking.integration.dlx}") String dlxName) {
        return deadLetterQueue(queueName, dlxName);
    }

    @Bean
    public Queue productQueue(
            @Value("${reporting.rabbitmq.queues.product:reporting.product.events}") String queueName,
            @Value("${reporting.rabbitmq.dead-letter-exchange:banking.integration.dlx}") String dlxName) {
        return deadLetterQueue(queueName, dlxName);
    }

    @Bean
    public Queue partyDeadLetterQueue(
            @Value("${reporting.rabbitmq.queues.party:reporting.party.events}") String queueName) {
        return QueueBuilder.durable(deadLetterQueueName(queueName)).build();
    }

    @Bean
    public Queue cardDeadLetterQueue(
            @Value("${reporting.rabbitmq.queues.card:reporting.card.events}") String queueName) {
        return QueueBuilder.durable(deadLetterQueueName(queueName)).build();
    }

    @Bean
    public Queue loanDeadLetterQueue(
            @Value("${reporting.rabbitmq.queues.loan:reporting.loan.events}") String queueName) {
        return QueueBuilder.durable(deadLetterQueueName(queueName)).build();
    }

    @Bean
    public Queue accountingDeadLetterQueue(
            @Value("${reporting.rabbitmq.queues.accounting:reporting.accounting.events}") String queueName) {
        return QueueBuilder.durable(deadLetterQueueName(queueName)).build();
    }

    @Bean
    public Queue productDeadLetterQueue(
            @Value("${reporting.rabbitmq.queues.product:reporting.product.events}") String queueName) {
        return QueueBuilder.durable(deadLetterQueueName(queueName)).build();
    }

    @Bean
    public Binding partyBinding(Queue partyQueue, TopicExchange integrationEventsExchange,
                              @Value("${reporting.rabbitmq.routing-keys.party:party.events}") String routingKey) {
        return BindingBuilder.bind(partyQueue).to(integrationEventsExchange).with(routingKey);
    }

    @Bean
    public Binding cardBinding(Queue cardQueue, TopicExchange integrationEventsExchange,
                             @Value("${reporting.rabbitmq.routing-keys.card:card.events}") String routingKey) {
        return BindingBuilder.bind(cardQueue).to(integrationEventsExchange).with(routingKey);
    }

    @Bean
    public Binding loanBinding(Queue loanQueue, TopicExchange integrationEventsExchange,
                             @Value("${reporting.rabbitmq.routing-keys.loan:loan.events}") String routingKey) {
        return BindingBuilder.bind(loanQueue).to(integrationEventsExchange).with(routingKey);
    }

    @Bean
    public Binding accountingBinding(Queue accountingQueue, TopicExchange integrationEventsExchange,
                                     @Value("${reporting.rabbitmq.routing-keys.accounting:accounting.events}") String routingKey) {
        return BindingBuilder.bind(accountingQueue).to(integrationEventsExchange).with(routingKey);
    }

    @Bean
    public Binding productBinding(Queue productQueue, TopicExchange integrationEventsExchange,
                                  @Value("${reporting.rabbitmq.routing-keys.product:product.events}") String routingKey) {
        return BindingBuilder.bind(productQueue).to(integrationEventsExchange).with(routingKey);
    }

    @Bean
    public Binding partyDeadLetterBinding(Queue partyDeadLetterQueue, DirectExchange deadLetterExchange) {
        return deadLetterBinding(partyDeadLetterQueue, deadLetterExchange);
    }

    @Bean
    public Binding cardDeadLetterBinding(Queue cardDeadLetterQueue, DirectExchange deadLetterExchange) {
        return deadLetterBinding(cardDeadLetterQueue, deadLetterExchange);
    }

    @Bean
    public Binding loanDeadLetterBinding(Queue loanDeadLetterQueue, DirectExchange deadLetterExchange) {
        return deadLetterBinding(loanDeadLetterQueue, deadLetterExchange);
    }

    @Bean
    public Binding accountingDeadLetterBinding(Queue accountingDeadLetterQueue, DirectExchange deadLetterExchange) {
        return deadLetterBinding(accountingDeadLetterQueue, deadLetterExchange);
    }

    @Bean
    public Binding productDeadLetterBinding(Queue productDeadLetterQueue, DirectExchange deadLetterExchange) {
        return deadLetterBinding(productDeadLetterQueue, deadLetterExchange);
    }

    /**
     * Delay queues used for retries: a failed message is parked here for {@code retry.delay-ms} and is then
     * dead-lettered (via the default exchange) back onto its original queue.
     */
    @Bean
    public Declarables retryQueues(
            @Value("${reporting.rabbitmq.queues.party:reporting.party.events}") String party,
            @Value("${reporting.rabbitmq.queues.card:reporting.card.events}") String card,
            @Value("${reporting.rabbitmq.queues.loan:reporting.loan.events}") String loan,
            @Value("${reporting.rabbitmq.queues.accounting:reporting.accounting.events}") String accounting,
            @Value("${reporting.rabbitmq.queues.product:reporting.product.events}") String product,
            @Value("${reporting.rabbitmq.retry.delay-ms:30000}") long delayMs) {
        return new Declarables(java.util.stream.Stream.of(party, card, loan, accounting, product)
                .map(queue -> (org.springframework.amqp.core.Declarable) QueueBuilder.durable(retryQueueName(queue))
                        .withArgument("x-message-ttl", delayMs)
                        .withArgument("x-dead-letter-exchange", "")
                        .withArgument("x-dead-letter-routing-key", queue)
                        .build())
                .toList());
    }

    public static String retryQueueName(String queueName) {
        return queueName + ".retry";
    }

    @Bean
    public SimpleRabbitListenerContainerFactory reportingRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    private Queue deadLetterQueue(String queueName, String dlxName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", deadLetterQueueName(queueName))
                .build();
    }

    private Binding deadLetterBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queue.getName());
    }

    private String deadLetterQueueName(String queueName) {
        return queueName + ".dlq";
    }
}
