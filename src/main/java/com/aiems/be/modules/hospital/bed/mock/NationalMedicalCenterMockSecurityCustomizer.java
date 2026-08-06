package com.aiems.be.modules.hospital.bed.mock;

import com.aiems.be.common.security.HttpSecurityCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class NationalMedicalCenterMockSecurityCustomizer implements HttpSecurityCustomizer {

    @Override
    public void customize(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/mock/**").permitAll());
    }
}
