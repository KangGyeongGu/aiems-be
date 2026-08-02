package com.A.B.modules.auth.oauth2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.oauth2")
public record OAuth2Properties(
        String successRedirectUri,
        String failureRedirectUri,
        Provider google,
        Provider kakao,
        Provider naver,
        Provider github
) {
    public record Provider(
            String clientId,
            String clientSecret
    ) {
    }
}
