package com.aiems.be.transfer.messaging;

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
    public Queue classificationReplyQueue() {
        return QueueBuilder.durable(AiMessaging.QUEUE_CLASSIFICATION_REPLY)
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
                .withArgument("x-dead-letter-routing-key", RabbitMQConfig.DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue summaryReplyQueue() {
        return QueueBuilder.durable(AiMessaging.QUEUE_SUMMARY_REPLY)
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
                .withArgument("x-dead-letter-routing-key", RabbitMQConfig.DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding classificationReplyBinding(Queue classificationReplyQueue, DirectExchange aiExchange) {
        return BindingBuilder.bind(classificationReplyQueue).to(aiExchange).with(AiMessaging.ROUTING_KEY_CLASSIFICATION_REPLY);
    }

    @Bean
    public Binding summaryReplyBinding(Queue summaryReplyQueue, DirectExchange aiExchange) {
        return BindingBuilder.bind(summaryReplyQueue).to(aiExchange).with(AiMessaging.ROUTING_KEY_SUMMARY_REPLY);
    }
}
