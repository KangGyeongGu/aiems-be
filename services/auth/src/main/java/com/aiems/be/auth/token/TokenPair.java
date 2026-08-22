package com.aiems.be.auth.token;

import java.time.Instant;

public record TokenPair(
        String accessToken,
        String refreshToken,
        Instant refreshExpiresAt
) {
}
