package com.aiems.be.contracts.notification;

public record NotificationMessage(
        String userId,
        String destination,
        String eventType,
        Object payload
) {
}
