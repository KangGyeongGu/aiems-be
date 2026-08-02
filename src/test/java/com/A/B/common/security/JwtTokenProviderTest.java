package com.A.B.common.security;

import com.A.B.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-that-is-at-least-32-bytes-long";

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            new JwtProperties(SECRET, Duration.ofMinutes(30), Duration.ofDays(14))
    );

    @Test
    @DisplayName("발급한 토큰에서 사용자 정보를 그대로 복원한다")
    void createAccessToken_thenParse_returnsSubjectAndRoles() {
        String token = jwtTokenProvider.createAccessToken("42", List.of("ROLE_USER", "ROLE_ADMIN"));

        Claims claims = jwtTokenProvider.parse(token);

        assertThat(jwtTokenProvider.getSubject(claims)).isEqualTo("42");
        assertThat(jwtTokenProvider.getRoles(claims)).containsExactly("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("만료된 토큰은 거부한다")
    void parse_withExpiredToken_throwsExpiredJwtException() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider(
                new JwtProperties(SECRET, Duration.ofSeconds(-60), Duration.ofDays(14))
        );

        String expiredToken = expiredProvider.createAccessToken("42", List.of("ROLE_USER"));

        assertThatThrownBy(() -> jwtTokenProvider.parse(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("허용 시계 오차 이내의 토큰은 통과시킨다")
    void parse_withinClockSkew_succeeds() {
        JwtTokenProvider justExpiredProvider = new JwtTokenProvider(
                new JwtProperties(SECRET, Duration.ofSeconds(-1), Duration.ofDays(14))
        );

        String token = justExpiredProvider.createAccessToken("42", List.of("ROLE_USER"));

        assertThat(jwtTokenProvider.parse(token).getSubject()).isEqualTo("42");
    }

    @Test
    @DisplayName("변조된 토큰은 거부한다")
    void parse_withTamperedToken_throwsJwtException() {
        String token = jwtTokenProvider.createAccessToken("42", List.of("ROLE_USER"));
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThatThrownBy(() -> jwtTokenProvider.parse(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰은 거부한다")
    void parse_withDifferentSecret_throwsJwtException() {
        String token = jwtTokenProvider.createAccessToken("42", List.of("ROLE_USER"));

        JwtTokenProvider otherProvider = new JwtTokenProvider(
                new JwtProperties("another-secret-key-that-is-at-least-32-bytes!!", Duration.ofMinutes(30), Duration.ofDays(14))
        );

        assertThatThrownBy(() -> otherProvider.parse(token))
                .isInstanceOf(JwtException.class);
    }
}
