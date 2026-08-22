package com.aiems.be.hospital.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.redis.service")
public record ServiceRedisProperties(
        String host,
        int port
) {
}
