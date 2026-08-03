package com.aiems.be.modules.auth.web;

import com.aiems.be.common.config.JwtProperties;
import com.aiems.be.common.config.RefreshTokenProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieManager {

    private final RefreshTokenProperties refreshTokenProperties;
    private final JwtProperties jwtProperties;

    public ResponseCookie create(String value) {
        RefreshTokenProperties.Cookie cookie = refreshTokenProperties.cookie();
        return ResponseCookie.from(cookie.name(), value)
                .httpOnly(true)
                .secure(cookie.secure())
                .path(cookie.path())
                .sameSite(cookie.sameSite())
                .maxAge(jwtProperties.refreshExpiration())
                .build();
    }

    public ResponseCookie expire() {
        RefreshTokenProperties.Cookie cookie = refreshTokenProperties.cookie();
        return ResponseCookie.from(cookie.name(), "")
                .httpOnly(true)
                .secure(cookie.secure())
                .path(cookie.path())
                .sameSite(cookie.sameSite())
                .maxAge(0)
                .build();
    }

    public Optional<String> read(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, refreshTokenProperties.cookie().name()))
                .map(Cookie::getValue);
    }
}
