package com.aiems.be.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secretKey,
        Duration accessExpiration,
        Duration refreshExpiration
) {
}
