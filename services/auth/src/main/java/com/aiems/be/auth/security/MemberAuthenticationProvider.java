package com.aiems.be.auth.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MemberAuthenticationProvider extends DaoAuthenticationProvider {

    public MemberAuthenticationProvider(MemberDetailsService memberDetailsService, PasswordEncoder passwordEncoder) {
        super(memberDetailsService);
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
             UsernamePasswordAuthenticationToken authentication
    ) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);

        if (!(authentication instanceof MemberAuthenticationToken token)) return;

        MemberDetails member = (MemberDetails) userDetails;

        if (member.getRole() != token.getExpectedRole()) {
            throw new BadCredentialsException("해당 계정 소속이 아닙니다.");
        }
    }
}
