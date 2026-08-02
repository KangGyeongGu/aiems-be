package com.A.B.modules.auth.oauth2.userinfo;

import com.A.B.modules.auth.domain.AuthProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.social.enabled", havingValue = "true")
public class OAuth2UserInfoFactory {

    private final ObjectMapper objectMapper;

    public OAuth2UserInfo create(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleUserInfo(objectMapper.convertValue(attributes, GoogleAttributes.class));
            case GITHUB -> new GithubUserInfo(objectMapper.convertValue(attributes, GithubAttributes.class));
            case NAVER -> new NaverUserInfo(objectMapper.convertValue(attributes, NaverAttributes.class));
            case KAKAO -> new KakaoUserInfo(objectMapper.convertValue(attributes, KakaoAttributes.class));
        };
    }
}
