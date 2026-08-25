package com.aiems.be.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@ConditionalOnProperty(name = "spring.datasource.url")
@EnableJpaAuditing
public class JpaConfig {
}
