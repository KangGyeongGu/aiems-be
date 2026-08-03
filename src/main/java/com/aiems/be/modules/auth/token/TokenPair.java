package com.aiems.be.modules.auth.token;

import java.time.Instant;

public record TokenPair(
        String accessToken,
        String refreshToken,
        Instant refreshExpiresAt
) {
}
