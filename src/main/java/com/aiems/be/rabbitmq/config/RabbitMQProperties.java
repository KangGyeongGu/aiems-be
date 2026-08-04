package com.aiems.be.rabbitmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMQProperties(
        String exchange,
        String routingKeySummary,
        String summaryQueue,
        String summaryReplyQueue,
        String routingKeyClassification,
        String classificationQueue
) {
}
