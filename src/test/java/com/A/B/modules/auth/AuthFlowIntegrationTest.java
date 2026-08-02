package com.A.B.modules.auth;

import com.A.B.support.IntegrationTestSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("인증 플로우 통합 테스트")
class AuthFlowIntegrationTest extends IntegrationTestSupport {

    private static final String PASSWORD = "password123";

    private String json(Map<String, String> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @Test
    @DisplayName("가입한 사용자는 로그인하고 토큰을 재발급받을 수 있다")
    void signup_thenLogin_thenReissue() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "flowuser", "password", PASSWORD,
                                "email", "flow@example.com", "username", "flow"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("flow@example.com"))
                .andExpect(jsonPath("$.data.username").value("flow"));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "flowuser", "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andReturn();

        String refreshValue = extractRefresh(loginResult);

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refresh_token", refreshValue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("비밀번호가 틀리면 로그인을 거부한다")
    void login_withWrongPassword_returns401() throws Exception {
        signup("wpuser");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "wpuser", "password", "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("이미 쓰는 loginId 가입은 거부한다")
    void signup_withDuplicateLoginId_returns409() throws Exception {
        signup("dupuser");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "dupuser", "password", PASSWORD))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("LOGIN_ID_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("로그아웃한 토큰으로는 재발급받을 수 없다")
    void logout_thenReissue_returns401() throws Exception {
        signup("logoutuser");
        String refresh = loginAndExtractRefresh("logoutuser");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("refresh_token", refresh)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refresh_token", refresh)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    @DisplayName("재발급은 토큰을 회전시켜 이전 토큰을 무효화한다")
    void reissue_rotatesToken_oldTokenBecomesInvalid() throws Exception {
        signup("rotateuser");
        String oldRefresh = loginAndExtractRefresh("rotateuser");

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refresh_token", oldRefresh)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refresh_token", oldRefresh)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    @DisplayName("소비된 토큰을 재사용하면 전 세션을 폐기한다")
    void consumedTokenReuse_revokesAllSessions() throws Exception {
        signup("reuseuser");
        String firstRefresh = loginAndExtractRefresh("reuseuser");

        MvcResult reissueResult = mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refresh_token", firstRefresh)))
                .andExpect(status().isOk())
                .andReturn();
        String rotatedRefresh = extractRefresh(reissueResult);

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refresh_token", firstRefresh)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refresh_token", rotatedRefresh)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    @DisplayName("탈퇴하면 기존 세션은 막히고 같은 아이디로 재가입은 열린다")
    void withdraw_thenLoginAndReissueRejected_andResignupAllowed() throws Exception {
        signup("wduser");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "wduser", "password", PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();
        String accessToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();
        String refresh = extractRefresh(loginResult);

        mockMvc.perform(delete("/api/v1/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "wduser", "password", PASSWORD))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refresh_token", refresh)))
                .andExpect(status().isUnauthorized());

        signup("wduser");
    }

    private void signup(String loginId) throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", loginId, "password", PASSWORD))))
                .andExpect(status().isCreated());
    }

    private String loginAndExtractRefresh(String loginId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", loginId, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();

        return extractRefresh(result);
    }

    private String extractRefresh(MvcResult result) {
        String setCookie = Objects.requireNonNull(
                result.getResponse().getHeader(HttpHeaders.SET_COOKIE), "Set-Cookie 없음");
        assertThat(setCookie).contains("refresh_token=");
        return setCookie.split(";", 2)[0].split("=", 2)[1];
    }
}
