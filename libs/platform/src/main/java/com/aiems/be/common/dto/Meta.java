package com.aiems.be.common.dto;

import java.time.Instant;

public record Meta(
        String requestId,
        String serverTime
) {
    public static Meta create(String requestId) {
        return new Meta(requestId, Instant.now().toString());
    }
}
