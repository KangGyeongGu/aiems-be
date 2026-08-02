package com.A.B.modules.auth.domain;

import java.util.Arrays;

public enum AuthProvider {
    GOOGLE,
    KAKAO,
    NAVER,
    GITHUB;

    public static AuthProvider from(String registrationId) {
        return Arrays.stream(values())
                .filter(provider -> provider.name().equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 제공자입니다: " + registrationId));
    }
}
