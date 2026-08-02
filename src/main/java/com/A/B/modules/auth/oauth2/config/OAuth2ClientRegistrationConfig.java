package com.A.B.modules.auth.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.social.enabled", havingValue = "true")
public class OAuth2ClientRegistrationConfig {

    private static final String REDIRECT_URI = "{baseUrl}/login/oauth2/code/{registrationId}";

    private final OAuth2Properties oAuth2Properties;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();
        addIfConfigured(registrations, oAuth2Properties.google(), this::googleRegistration);
        addIfConfigured(registrations, oAuth2Properties.github(), this::githubRegistration);
        addIfConfigured(registrations, oAuth2Properties.naver(), this::naverRegistration);
        addIfConfigured(registrations, oAuth2Properties.kakao(), this::kakaoRegistration);

        if (registrations.isEmpty()) {
            throw new IllegalStateException("소셜 로그인이 활성화되었으나 구성된 Provider가 없습니다.");
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private void addIfConfigured(
            List<ClientRegistration> registrations,
            OAuth2Properties.Provider provider,
            Function<OAuth2Properties.Provider, ClientRegistration> factory
    ) {
        if (provider != null && StringUtils.hasText(provider.clientId())) {
            registrations.add(factory.apply(provider));
        }
    }

    private ClientRegistration googleRegistration(OAuth2Properties.Provider provider) {
        return CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId(provider.clientId())
                .clientSecret(provider.clientSecret())
                .build();
    }

    private ClientRegistration githubRegistration(OAuth2Properties.Provider provider) {
        return CommonOAuth2Provider.GITHUB.getBuilder("github")
                .clientId(provider.clientId())
                .clientSecret(provider.clientSecret())
                .scope("read:user", "user:email")
                .build();
    }

    private ClientRegistration naverRegistration(OAuth2Properties.Provider provider) {
        return ClientRegistration.withRegistrationId("naver")
                .clientId(provider.clientId())
                .clientSecret(provider.clientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(REDIRECT_URI)
                .scope("email", "nickname")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .clientName("Naver")
                .build();
    }

    private ClientRegistration kakaoRegistration(OAuth2Properties.Provider provider) {
        return ClientRegistration.withRegistrationId("kakao")
                .clientId(provider.clientId())
                .clientSecret(provider.clientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(REDIRECT_URI)
                .scope("account_email", "profile_nickname")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientName("Kakao")
                .build();
    }
}
