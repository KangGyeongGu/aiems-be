package com.A.B.websocket.event;

public record EventEnvelope<T>(
        EventType type,
        T data,
        EventMeta meta
) {
    public static <T> EventEnvelope<T> of(EventType type, T data) {
        return new EventEnvelope<>(type, data, EventMeta.create());
    }
}
