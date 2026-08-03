package com.aiems.be.websocket.event;

import com.aiems.be.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true")
public class StompEventPublisher {

    private static final String QUEUE_ERRORS = "/queue/errors";

    private final SimpMessagingTemplate messagingTemplate;

    public <T> void broadcast(String topic, EventType type, T data) {
        messagingTemplate.convertAndSend(topic, EventEnvelope.of(type, data));
    }

    public <T> void sendToUser(String userId, String destination, EventType type, T data) {
        messagingTemplate.convertAndSendToUser(userId, destination, EventEnvelope.of(type, data));
    }

    public void sendError(String userId, ErrorCode errorCode, String message) {
        Map<String, Object> errorData = Map.of(
                "code", errorCode.getCode(),
                "message", message,
                "details", Map.of()
        );

        sendToUser(userId, QUEUE_ERRORS, EventType.ERROR, errorData);
    }
}
