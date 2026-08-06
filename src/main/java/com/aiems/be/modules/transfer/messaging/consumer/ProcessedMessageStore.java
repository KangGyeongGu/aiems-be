package com.aiems.be.modules.transfer.messaging.consumer;

import com.aiems.be.config.ServiceRedis;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ProcessedMessageStore {

    private static final String KEY_PREFIX = "processed:message:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    public ProcessedMessageStore(@ServiceRedis StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isProcessed(String messageId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + messageId));
    }

    public void mark(String messageId) {
        redisTemplate.opsForValue().set(KEY_PREFIX + messageId, "1", TTL);
    }
}
