package com.aiems.be.hospital.security;

import com.aiems.be.common.domain.Role;
import com.aiems.be.common.security.HttpSecurityCustomizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class HospitalSecurityCustomizer implements HttpSecurityCustomizer {

    @Override
    public void customize(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/internal/**").permitAll()
                .requestMatchers("/api/v1/hospital/**").hasAuthority(Role.HOSPITAL.authority()));
    }
}
