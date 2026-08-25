package com.aiems.be.transfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class ServiceRedisConfig {

    @Bean
    @ServiceRedis
    public RedisConnectionFactory serviceRedisConnectionFactory(ServiceRedisProperties properties) {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(properties.host(), properties.port()));
    }

    @Bean
    @ServiceRedis
    public StringRedisTemplate serviceStringRedisTemplate(@ServiceRedis RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
