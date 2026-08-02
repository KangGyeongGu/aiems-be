package com.A.B.modules.auth.token.store;

import com.A.B.config.AuthRedis;
import com.A.B.modules.auth.token.RefreshTokenHasher;
import com.A.B.modules.auth.token.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.refresh-token.store", havingValue = "redis")
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private static final String TOKEN_KEY_PREFIX = "refresh:token:";
    private static final String MEMBER_KEY_PREFIX = "refresh:member:";
    private static final String USED_KEY_PREFIX = "refresh:used:";

    @AuthRedis private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(String memberId, String rawToken, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (ttl.isZero() || ttl.isNegative()) return;

        String tokenHash = RefreshTokenHasher.hash(rawToken);
        stringRedisTemplate.opsForValue().set(tokenKey(tokenHash), memberId, ttl);
        stringRedisTemplate.opsForSet().add(memberKey(memberId), tokenHash);
        stringRedisTemplate.expire(memberKey(memberId), ttl);
    }


    @Override
    public Optional<Long> consume(String rawToken) {
        String tokenHash = RefreshTokenHasher.hash(rawToken);

        long remainingSeconds = stringRedisTemplate.getExpire(tokenKey(tokenHash), TimeUnit.SECONDS);
        String memberId = stringRedisTemplate.opsForValue().getAndDelete(tokenKey(tokenHash));

        if (memberId != null) {
            stringRedisTemplate.opsForSet().remove(memberKey(memberId), tokenHash);

            if (remainingSeconds > 0) {
                stringRedisTemplate.opsForValue().set(useKey(tokenHash), memberId, Duration.ofSeconds(remainingSeconds));
            }
            return Optional.of(Long.valueOf(memberId));
        }

        String reusedMemberId = stringRedisTemplate.opsForValue().get(useKey(tokenHash));

        if (reusedMemberId != null) {
            log.warn("Refresh Token reuse detected: memberId={}", reusedMemberId);
            deleteByMemberId(reusedMemberId);
        }

        return Optional.empty();
    }

    @Override
    public void delete(String rawToken) {
        String tokenHash = RefreshTokenHasher.hash(rawToken);
        String memberId = stringRedisTemplate.opsForValue().get(tokenKey(tokenHash));
        stringRedisTemplate.delete(tokenKey(tokenHash));

        if (memberId != null) {
            stringRedisTemplate.opsForSet().remove(memberKey(memberId), tokenHash);
        }
    }

    @Override
    public void deleteByMemberId(String memberId) {
        Set<String> tokenHashes = stringRedisTemplate.opsForSet().members(memberKey(memberId));

        List<String> keys = new ArrayList<>();
        if (tokenHashes != null) {
            tokenHashes.forEach(hash -> keys.add(tokenKey(hash)));
        }
        keys.add(memberKey(memberId));

        stringRedisTemplate.delete(keys);
    }

    private String tokenKey(String tokenHash) {
        return TOKEN_KEY_PREFIX + tokenHash;
    }

    private String memberKey(String memberId) {
        return MEMBER_KEY_PREFIX + memberId;
    }

    private String useKey(String tokenHash) {
        return USED_KEY_PREFIX + tokenHash;
    }
}
