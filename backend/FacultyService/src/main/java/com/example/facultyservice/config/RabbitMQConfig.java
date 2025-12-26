package com.example.facultyservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import jakarta.annotation.PostConstruct;

/**
 * RabbitMQ Configuration for Faculty Service
 * 
 * Purpose:
 * - Configure message exchange, queues and bindings
 * - Set up message converter for JSON serialization
 * - Configure retry policy for failed messages
 * - Enable asynchronous communication with other services
 * 
 * Message Flow:
 * Faculty Service → RabbitMQ Exchange → Queue → Student Service
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue}")
    private String queue;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("RabbitMQ Configuration Initializing...");
        logger.info("Exchange: {}", exchange);
        logger.info("Queue: {}", queue);
        logger.info("Routing Key: {}", routingKey);
        logger.info("=================================================");
    }

    /**
     * Define the main queue for faculty service
     * Durable = true means queue survives broker restart
     */
    @Bean
    public Queue facultyQueue() {
        logger.info("Creating queue: {}", queue);
        return QueueBuilder.durable(queue)
                .withArgument("x-message-ttl", 86400000) // 24 hours TTL
                .withArgument("x-max-length", 10000) // Max 10000 messages
                .build();
    }

    /**
     * Define topic exchange for routing messages
     * Topic exchange allows pattern matching in routing keys
     */
    @Bean
    public TopicExchange facultyExchange() {
        logger.info("Creating topic exchange: {}", exchange);
        return new TopicExchange(exchange, true, false);
    }

    /**
     * Bind queue to exchange with routing key
     * Messages sent to exchange with this routing key will go to the queue
     */
    @Bean
    public Binding facultyBinding(Queue facultyQueue, TopicExchange facultyExchange) {
        logger.info("Binding queue '{}' to exchange '{}' with routing key '{}'",
                queue, exchange, routingKey);
        return BindingBuilder
                .bind(facultyQueue)
                .to(facultyExchange)
                .with(routingKey);
    }

    /**
     * Dead Letter Queue for failed messages
     * Messages that fail processing are sent here
     */
    @Bean
    public Queue deadLetterQueue() {
        String dlqName = queue + ".dlq";
        logger.info("Creating dead letter queue: {}", dlqName);
        return QueueBuilder.durable(dlqName).build();
    }

    /**
     * Dead Letter Exchange
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        String dlxName = exchange + ".dlx";
        logger.info("Creating dead letter exchange: {}", dlxName);
        return new DirectExchange(dlxName);
    }

    /**
     * Bind DLQ to DLX
     */
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(queue + ".dlq");
    }

    /**
     * JSON Message Converter
     * Converts Java objects to JSON for message serialization
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        logger.info("Configuring Jackson2JsonMessageConverter");
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate for sending messages
     * Configured with JSON converter and retry policy
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        logger.info("Configuring RabbitTemplate with retry policy");

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setRetryTemplate(retryTemplate());

        // Enable mandatory flag to get returns for unroutable messages
        template.setMandatory(true);

        // Callback for returned messages
        template.setReturnsCallback(returned -> {
            logger.warn("Message returned: Exchange={}, RoutingKey={}, ReplyText={}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText());
        });

        // Callback for publisher confirms
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                logger.debug("Message sent successfully. CorrelationData: {}", correlationData);
            } else {
                logger.error("Message send failed. Cause: {}, CorrelationData: {}",
                        cause, correlationData);
            }
        });

        logger.info("✓ RabbitTemplate configured successfully");
        return template;
    }

    /**
     * Retry Template for failed message sends
     * Exponential backoff: 1s, 2s, 4s
     */
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Retry 3 times
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        // Exponential backoff
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000); // 1 second
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000); // 10 seconds
        retryTemplate.setBackOffPolicy(backOffPolicy);

        logger.info("✓ Retry template configured: max 3 attempts with exponential backoff");
        return retryTemplate;
    }

    /**
     * Listener Container Factory for message consumers
     * Configures how messages are received and processed
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setPrefetchCount(10); // Process 10 messages at a time
        factory.setConcurrentConsumers(3); // 3 concurrent consumers
        factory.setMaxConcurrentConsumers(10); // Scale up to 10
        factory.setDefaultRequeueRejected(false); // Don't requeue failed messages

        logger.info("✓ Listener container factory configured");
        return factory;
    }
}
