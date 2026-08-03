package com.aiems.be.security;

import com.aiems.be.common.config.JwtProperties;
import com.aiems.be.common.security.JwtTokenProvider;
import com.aiems.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityIntegrationTest.TestProtectedController.class)
@DisplayName("보안 필터·인가 통합 테스트")
class SecurityIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtProperties jwtProperties;

    @RestController
    static class TestProtectedController {

        @GetMapping("/api/v1/ping")
        public String ping() {
            return "pong";
        }

        @GetMapping("/api/v1/admin")
        @PreAuthorize("hasRole('ADMIN')")
        public String admin() {
            return "admin";
        }
    }

    @Test
    @DisplayName("인증 없이 보호 자원에 접근하면 차단한다")
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
                .andExpect(header().exists("X-Request-ID"))
                .andExpect(jsonPath("$.meta.requestId").isNotEmpty());
    }

    @Test
    @DisplayName("만료된 토큰의 접근은 차단한다")
    void protectedEndpoint_withExpiredToken_returnsTokenExpired() throws Exception {
        JwtTokenProvider expiredProvider = new JwtTokenProvider(
                new JwtProperties(jwtProperties.secretKey(), Duration.ofSeconds(-60), Duration.ofDays(14))
        );
        String expiredToken = expiredProvider.createAccessToken("1", List.of("ROLE_USER"));

        mockMvc.perform(get("/api/v1/ping").header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("TOKEN_EXPIRED"));
    }

    @Test
    @DisplayName("유효한 토큰이면 보호 자원을 허용한다")
    void protectedEndpoint_withUserToken_returns200() throws Exception {
        String token = jwtTokenProvider.createAccessToken("1", List.of("ROLE_USER"));

        mockMvc.perform(get("/api/v1/ping").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("권한이 부족하면 접근을 차단한다")
    void adminEndpoint_withUserToken_returns403() throws Exception {
        String token = jwtTokenProvider.createAccessToken("1", List.of("ROLE_USER"));

        mockMvc.perform(get("/api/v1/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("권한이 충분하면 접근을 허용한다")
    void adminEndpoint_withAdminToken_returns200() throws Exception {
        String token = jwtTokenProvider.createAccessToken("1", List.of("ROLE_ADMIN"));

        mockMvc.perform(get("/api/v1/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
