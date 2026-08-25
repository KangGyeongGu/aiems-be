package com.aiems.be.hospital.messaging;

import com.aiems.be.contracts.notification.NotificationMessage;
import com.aiems.be.contracts.websocket.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    public static final String NOTIFICATION_EXCHANGE = "notification_exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notify";

    private final RabbitTemplate rabbitTemplate;

    public void sendToUser(String userId, String destination, EventType eventType, Object payload) {
        rabbitTemplate.convertAndSend(
                NOTIFICATION_EXCHANGE,
                NOTIFICATION_ROUTING_KEY,
                new NotificationMessage(userId, destination, eventType.name(), payload));
    }

    @Configuration
    static class NotificationExchangeConfig {
        @Bean
        public DirectExchange notificationExchange() {
            return new DirectExchange(NOTIFICATION_EXCHANGE);
        }
    }
}
