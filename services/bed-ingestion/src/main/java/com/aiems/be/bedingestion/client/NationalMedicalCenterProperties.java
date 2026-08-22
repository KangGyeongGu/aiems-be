package com.aiems.be.bedingestion.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openapi.nmc")
public record NationalMedicalCenterProperties(
        String apiKey,
        String baseUrl
) {
}
