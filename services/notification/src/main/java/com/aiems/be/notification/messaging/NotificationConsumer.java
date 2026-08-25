package com.aiems.be.notification.messaging;

import com.aiems.be.contracts.notification.NotificationMessage;
import com.aiems.be.notification.websocket.event.StompEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final StompEventPublisher stompEventPublisher;

    @RabbitListener(queues = NotificationMessagingConfig.NOTIFICATION_QUEUE)
    public void onNotification(NotificationMessage message) {
        stompEventPublisher.sendToUser(
                message.userId(),
                message.destination(),
                message.eventType(),
                message.payload());
    }
}
