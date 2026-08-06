package com.aiems.be.websocket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "websocket.relay")
public record StompRelayProperties(
        String host,
        int port,
        String username,
        String password,
        String virtualHost
) {
}
