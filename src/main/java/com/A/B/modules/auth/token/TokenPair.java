package com.A.B.modules.auth.token;

import java.time.Instant;

public record TokenPair(
        String accessToken,
        String refreshToken,
        Instant refreshExpiresAt
) {
}
