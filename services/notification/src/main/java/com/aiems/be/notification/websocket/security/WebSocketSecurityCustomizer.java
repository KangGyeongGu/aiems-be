package com.aiems.be.notification.websocket.security;

import com.aiems.be.common.security.HttpSecurityCustomizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSecurityCustomizer implements HttpSecurityCustomizer {

    @Override
    public void customize(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/ws/**").permitAll());
    }
}
