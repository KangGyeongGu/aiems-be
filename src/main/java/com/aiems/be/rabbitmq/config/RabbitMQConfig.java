package com.aiems.be.rabbitmq.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    public static final String DLX_NAME = "ai_exchange.dlx";
    public static final String DLQ_NAME = "patient_dlq";
    public static final String DLQ_ROUTING_KEY = "patient.dead";
    public static final String ROUTING_KEY_SUMMARY_REPLY = "patient.summary_reply";

    private final RabbitMQProperties properties;

    @Bean
    public DirectExchange aiExchange() {
        return new DirectExchange(properties.exchange());
    }

    @Bean
    public Queue summaryQueue() {
        return QueueBuilder.durable(properties.summaryQueue())
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue classificationQueue() {
        return QueueBuilder.durable(properties.classificationQueue())
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue summaryReplyQueue() {
        return QueueBuilder.durable(properties.summaryReplyQueue())
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding summaryBinding(Queue summaryQueue, DirectExchange aiExchange) {
        return BindingBuilder.bind(summaryQueue).to(aiExchange).with(properties.routingKeySummary());
    }

    @Bean
    public Binding classificationBinding(Queue classificationQueue, DirectExchange aiExchange) {
        return BindingBuilder.bind(classificationQueue).to(aiExchange).with(properties.routingKeyClassification());
    }

    @Bean
    public Binding summaryReplyBinding(Queue summaryReplyQueue, DirectExchange aiExchange) {
        return BindingBuilder.bind(summaryReplyQueue).to(aiExchange).with(ROUTING_KEY_SUMMARY_REPLY);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_NAME);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ_NAME);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setReplyTimeout(60000);

        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback((correlation, ack, cause) -> {
            if (!ack) {
                log.error("메시지 발행 확인 실패. correlation={}, cause={}", correlation != null ? correlation.getId() : "none", cause);
            }
        });

        rabbitTemplate.setReturnsCallback(returned ->
                log.error("메시지 라우팅 실패. exchange={}, routingKey={}, message={}",
                        returned.getExchange(), returned.getRoutingKey(), returned.getMessage()));

        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setPrefetchCount(10);
        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(3)
                        .backOffOptions(1000, 2.0, 10000)
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .build()
        );

        return factory;

    }
}
