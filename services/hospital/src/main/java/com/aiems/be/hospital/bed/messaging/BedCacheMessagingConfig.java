package com.aiems.be.hospital.bed.messaging;

import com.aiems.be.contracts.messaging.BedMessaging;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BedCacheMessagingConfig {

    @Bean
    public DirectExchange bedExchange() {
        return new DirectExchange(BedMessaging.EXCHANGE);
    }

    @Bean
    public Queue bedUpdatedQueue() {
        return QueueBuilder.durable(BedMessaging.QUEUE_BED_UPDATED).build();
    }

    @Bean
    public Binding bedUpdatedBinding(Queue bedUpdatedQueue, DirectExchange bedExchange) {
        return BindingBuilder.bind(bedUpdatedQueue).to(bedExchange).with(BedMessaging.ROUTING_KEY_BED_UPDATED);
    }
}
