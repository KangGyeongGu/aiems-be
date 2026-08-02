package com.A.B.modules.auth.token.store;

import com.A.B.modules.auth.domain.RefreshToken;
import com.A.B.modules.auth.repository.RefreshTokenRepository;
import com.A.B.modules.auth.token.RefreshTokenHasher;
import com.A.B.modules.auth.token.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.refresh-token.store", havingValue = "rdb")
public class RdbRefreshTokenStore implements RefreshTokenStore {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void save(String memberId, String rawToken, Instant expiresAt) {
        RefreshToken refreshToken = RefreshToken.create(
                Long.valueOf(memberId),
                RefreshTokenHasher.hash(rawToken),
                expiresAt
        );
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public Optional<Long> consume(String rawToken) {
        String tokenHash = RefreshTokenHasher.hash(rawToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .orElse(null);
        if (token == null) return Optional.empty();

        if (token.isUsed()) {
            log.warn("Refresh token reuse detected: memberId={}", token.getMemberId());
            refreshTokenRepository.deleteByMemberId(token.getMemberId());
            return Optional.empty();
        }

        int rows = refreshTokenRepository.markUsed(tokenHash, Instant.now());
        return rows == 1 ? Optional.of(token.getMemberId()) : Optional.empty();
    }

    @Override
    @Transactional
    public void delete(String rawToken) {
        refreshTokenRepository.deleteByTokenHash(RefreshTokenHasher.hash(rawToken));
    }

    @Override
    @Transactional
    public void deleteByMemberId(String memberId) {
        refreshTokenRepository.deleteByMemberId(Long.valueOf(memberId));
    }

    @Transactional
    public int purgeExpired() {
        return refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
