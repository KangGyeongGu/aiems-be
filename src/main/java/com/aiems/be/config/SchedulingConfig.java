package com.aiems.be.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT9M")
@ConditionalOnProperty(name = "app.role.batch.enabled", havingValue = "true")
public class SchedulingConfig {

    @Bean
    public LockProvider lockProvider(@ServiceRedis RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory);
    }
}
