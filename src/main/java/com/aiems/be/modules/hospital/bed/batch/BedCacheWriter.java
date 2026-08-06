package com.aiems.be.modules.hospital.bed.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@RequiredArgsConstructor
public class BedCacheWriter implements ItemWriter<BedCacheEntry> {

    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;

    @Override
    public void write(Chunk<? extends BedCacheEntry> chunk) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            chunk.getItems().forEach(entry ->
                connection.stringCommands().setEx(
                        entry.key().getBytes(StandardCharsets.UTF_8),
                        CACHE_TTL.toSeconds(),
                        entry.value().getBytes(StandardCharsets.UTF_8)));
            return null;
        });
    }
}
