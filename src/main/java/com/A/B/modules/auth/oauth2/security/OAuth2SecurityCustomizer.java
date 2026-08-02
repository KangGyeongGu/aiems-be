package com.A.B.modules.auth.oauth2.security;

import com.A.B.common.security.HttpSecurityCustomizer;
import com.A.B.modules.auth.oauth2.handler.OAuth2FailureHandler;
import com.A.B.modules.auth.oauth2.handler.OAuth2SuccessHandler;
import com.A.B.modules.auth.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.A.B.modules.auth.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.social.enabled", havingValue = "true")
public class OAuth2SecurityCustomizer implements HttpSecurityCustomizer {

    private final ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Override
    public void customize(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/oauth2/**", "/login/**").permitAll());

        if (clientRegistrationRepository.getIfAvailable() == null) return;

        httpSecurity.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint
                        .authorizationRequestRepository(authorizationRequestRepository))
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler));
    }
}
