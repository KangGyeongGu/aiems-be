package com.aiems.be.auth.security;

import com.aiems.be.common.domain.Role;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class MemberAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final Role expectedRole;

    private MemberAuthenticationToken(String loginId, String password, Role expectedRole) {
        super(loginId, password);
        this.expectedRole = expectedRole;
    }

    public static MemberAuthenticationToken unauthenticated(String loginId, String password, Role expectedRole) {
        return new MemberAuthenticationToken(loginId, password, expectedRole);
    }
}
