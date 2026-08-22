package com.aiems.be.contracts.websocket;

import java.time.Instant;
import java.util.UUID;

public record EventMeta(
        String eventId,
        String serverTime
) {
    public static EventMeta create() {
        return new EventMeta(UUID.randomUUID().toString(), Instant.now().toString());
    }
}
