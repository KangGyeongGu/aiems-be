package com.A.B.modules.auth.service;

import com.A.B.common.config.JwtProperties;
import com.A.B.common.security.JwtTokenProvider;
import com.A.B.modules.auth.domain.Role;
import com.A.B.modules.auth.token.RefreshTokenGenerator;
import com.A.B.modules.auth.token.RefreshTokenStore;
import com.A.B.modules.auth.token.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenStore refreshTokenStore;
    private final RefreshTokenGenerator refreshTokenGenerator;

    public TokenPair issue(Long memberId, Role role) {
        String accessToken = jwtTokenProvider.createAccessToken(
                String.valueOf(memberId),
                List.of(role.authority())
        );

        String rawRefreshToken = refreshTokenGenerator.generate();
        Instant expiresAt = Instant.now().plus(jwtProperties.refreshExpiration());

        refreshTokenStore.save(String.valueOf(memberId), rawRefreshToken, expiresAt);

        return new TokenPair(accessToken, rawRefreshToken, expiresAt);
    }

    public Optional<Long> consume(String rawRefreshToken) {
        return refreshTokenStore.consume(rawRefreshToken);
    }

    public void revoke(String rawRefreshToken) {
        refreshTokenStore.delete(rawRefreshToken);
    }

    public void revokeAll(Long memberId) {
        refreshTokenStore.deleteByMemberId(String.valueOf(memberId));
    }
}
