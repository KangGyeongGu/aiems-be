package com.A.B.modules.auth.security;

import com.A.B.common.security.HttpSecurityCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.auth.local.enabled", havingValue = "true")
public class LocalAuthSecurityCustomizer implements HttpSecurityCustomizer {

    @Override
    public void customize(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/signup", "/api/v1/auth/login").permitAll());
    }
}
