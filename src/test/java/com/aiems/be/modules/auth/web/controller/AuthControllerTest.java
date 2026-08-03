package com.aiems.be.modules.auth.web.controller;

import com.aiems.be.common.config.JwtProperties;
import com.aiems.be.modules.auth.domain.Member;
import com.aiems.be.modules.auth.service.AuthService;
import com.aiems.be.modules.auth.token.TokenPair;
import com.aiems.be.modules.auth.web.RefreshTokenCookieManager;
import com.aiems.be.modules.auth.web.TokenResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({LocalAuthController.class, TokenController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(TokenResponseWriter.class)
@DisplayName("인증 컨트롤러 웹 슬라이스 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private RefreshTokenCookieManager refreshTokenCookieManager;
    @MockitoBean
    private JwtProperties jwtProperties;

    private String json(Map<String, String> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @Test
    @DisplayName("회원가입 성공은 201과 공통 응답 형식으로 내려준다")
    void signup_returns201AndEnvelope() throws Exception {
        given(authService.signup(any(), any(), any(), any())).willReturn(Member.create("a@b.com", "nick"));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "gildong", "password", "password123", "email", "a@b.com", "username", "nick"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("a@b.com"))
                .andExpect(jsonPath("$.data.username").value("nick"))
                .andExpect(jsonPath("$.meta.serverTime").exists());
    }

    @Test
    @DisplayName("형식에 맞지 않는 loginId는 400으로 거부한다")
    void signup_withInvalidLoginId_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "ab", "password", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("로그인은 access는 본문에, refresh는 쿠키로 내려준다")
    void login_setsRefreshCookieAndReturnsAccessToken() throws Exception {
        given(authService.login(any(), any()))
                .willReturn(new TokenPair("access-token", "refresh-token", Instant.now().plusSeconds(60)));
        given(refreshTokenCookieManager.create("refresh-token"))
                .willReturn(ResponseCookie.from("refresh_token", "refresh-token").path("/").build());
        given(jwtProperties.accessExpiration()).willReturn(Duration.ofMinutes(30));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "gildong", "password", "password123"))))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=refresh-token")))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("리프레시 쿠키 없는 재발급은 401로 거부한다")
    void reissue_withoutCookie_returns401() throws Exception {
        given(refreshTokenCookieManager.read(any())).willReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/auth/reissue"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    @DisplayName("형식에 맞지 않는 이메일은 400으로 거부한다")
    void signup_withInvalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("loginId", "member01", "password", "password123", "email", "not-an-email"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("깨진 요청 본문은 400으로 거부한다")
    void signup_withMalformedJson_returns400() throws Exception {
        // 의도적 JSON: 파서 단계 400에러 경로 검증
        //noinspection JsonStandardCompliance
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not valid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST"));
    }

    @Test
    @DisplayName("허용되지 않은 메서드는 405로 거부한다")
    void login_withUnsupportedMethod_returns405() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error.code").value("METHOD_NOT_ALLOWED"));
    }
}
