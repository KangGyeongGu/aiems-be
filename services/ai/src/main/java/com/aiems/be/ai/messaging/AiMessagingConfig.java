package com.aiems.be.ai.messaging;

import com.aiems.be.contracts.messaging.AiMessaging;
import com.aiems.be.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiMessagingConfig {

    @Bean
    public DirectExchange aiExchange() {
        return new DirectExchange(AiMessaging.EXCHANGE);
    }

    @Bean
    public Queue classificationQueue() {
        return QueueBuilder.durable(AiMessaging.QUEUE_CLASSIFICATION)
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
                .withArgument("x-dead-letter-routing-key", RabbitMQConfig.DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue summaryQueue() {
        return QueueBuilder.durable(AiMessaging.QUEUE_SUMMARY)
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
                .withArgument("x-dead-letter-routing-key", RabbitMQConfig.DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding classificationBinding(Queue classificationQueue, DirectExchange aiExchange) {
        return BindingBuilder.bind(classificationQueue).to(aiExchange).with(AiMessaging.ROUTING_KEY_CLASSIFICATION);
    }

    @Bean
    public Binding summaryBinding(Queue summaryQueue, DirectExchange aiExchange) {
        return BindingBuilder.bind(summaryQueue).to(aiExchange).with(AiMessaging.ROUTING_KEY_SUMMARY);
    }
}
