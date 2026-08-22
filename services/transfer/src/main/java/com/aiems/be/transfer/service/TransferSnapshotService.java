package com.aiems.be.transfer.service;

import com.aiems.be.transfer.config.ServiceRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransferSnapshotService {

    private static final String KEY_PREFIX = "transfer:request:snapshot:";
    private static final Duration TTL = Duration.ofMinutes(30);

    @ServiceRedis
    private final StringRedisTemplate redisTemplate;

    public void save(Long ambulanceId, List<Long> hospitalIds) {
        String key = key(ambulanceId);
        redisTemplate.delete(key);

        String[] members = hospitalIds.stream().map(String::valueOf).toArray(String[]::new);
        redisTemplate.opsForSet().add(key, members);
        redisTemplate.expire(key, TTL);
    }

    public List<Long> find(Long ambulanceId) {
        Set<String> members = redisTemplate.opsForSet().members(key(ambulanceId));
        if (members == null) {
            return List.of();
        }
        return members.stream().map(Long::valueOf).toList();
    }

    private String key(Long ambulanceId) {
        return KEY_PREFIX + ambulanceId;
    }
}
