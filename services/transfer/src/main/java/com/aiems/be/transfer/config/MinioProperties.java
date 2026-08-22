package com.aiems.be.transfer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.minio")
public record MinioProperties(
        String endpoint,
        String region,
        String accessKey,
        String secretKey,
        String voiceBucket,
        String reportBucket,
        int presignTtlMinutes
) {
}
