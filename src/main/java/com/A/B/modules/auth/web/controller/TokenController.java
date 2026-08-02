package com.A.B.modules.auth.web.controller;

import com.A.B.common.dto.ApiResponse;
import com.A.B.common.exception.BusinessException;
import com.A.B.modules.auth.exception.AuthErrorCode;
import com.A.B.modules.auth.service.AuthService;
import com.A.B.modules.auth.token.TokenPair;
import com.A.B.modules.auth.web.RefreshTokenCookieManager;
import com.A.B.modules.auth.web.TokenResponseWriter;
import com.A.B.modules.auth.web.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;
    private final RefreshTokenCookieManager refreshTokenCookieManager;
    private final TokenResponseWriter tokenResponseWriter;

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request) {
        String rawRefreshToken = refreshTokenCookieManager.read(request)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        TokenPair tokens = authService.reissue(rawRefreshToken);
        return tokenResponseWriter.write(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        refreshTokenCookieManager.read(request).ifPresent(authService::logout);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieManager.expire().toString())
                .build();
    }
}
