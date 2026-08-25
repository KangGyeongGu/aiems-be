package com.aiems.be.transfer.security;

import com.aiems.be.common.domain.Role;
import com.aiems.be.common.security.HttpSecurityCustomizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class TransferSecurityCustomizer implements HttpSecurityCustomizer {

    @Override
    public void customize(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/internal/**").permitAll()
                .requestMatchers("/api/v1/transfers/**").hasAuthority(Role.AMBULANCE.authority()));
    }
}
