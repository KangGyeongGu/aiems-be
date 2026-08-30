package com.aiems.be.common.dto;

import java.time.Instant;

public record Meta(
        String serverTime
) {
    public static Meta create() {
        return new Meta(Instant.now().toString());
    }
}
