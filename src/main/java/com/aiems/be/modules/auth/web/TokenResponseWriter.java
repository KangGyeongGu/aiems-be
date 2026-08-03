package com.aiems.be.modules.auth.web;

import com.aiems.be.common.config.JwtProperties;
import com.aiems.be.modules.auth.token.TokenPair;
import com.aiems.be.modules.auth.web.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenResponseWriter {

    private final RefreshTokenCookieManager refreshTokenCookieManager;
    private final JwtProperties jwtProperties;

    public ResponseEntity<TokenResponse> write(TokenPair tokens) {
        ResponseCookie refreshCookie = refreshTokenCookieManager.create(tokens.refreshToken());
        TokenResponse body = TokenResponse.of(tokens.accessToken(), jwtProperties.accessExpiration().toSeconds());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(body);
    }
}
