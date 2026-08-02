package com.A.B.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.refresh-token")
public record RefreshTokenProperties(
        Cookie cookie
) {
    public record Cookie(
            String name,
            String path,
            boolean secure,
            String sameSite
    ) {
    }
}
