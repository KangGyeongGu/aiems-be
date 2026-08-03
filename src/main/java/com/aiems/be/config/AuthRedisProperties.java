package com.aiems.be.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.redis.auth")
public record AuthRedisProperties(
        String host,
        int port
) {
}
