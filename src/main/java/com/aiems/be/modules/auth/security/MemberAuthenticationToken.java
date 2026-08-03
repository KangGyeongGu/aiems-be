package com.aiems.be.modules.auth.security;

import com.aiems.be.common.domain.Role;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class MemberAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final Role expectedRole;
    private final String deviceId;

    private MemberAuthenticationToken(String loginId, String password, Role expectedRole, String deviceId) {
        super(loginId, password);
        this.expectedRole = expectedRole;
        this.deviceId = deviceId;
    }

    public static MemberAuthenticationToken unauthenticated(String loginId, String password, Role expectedRole) {
        return new MemberAuthenticationToken(loginId, password, expectedRole, null);
    }

    public static MemberAuthenticationToken unauthenticatedAmbulance(String loginId, String password, String deviceId) {
        return new MemberAuthenticationToken(loginId, password, Role.AMBULANCE, deviceId);
    }
}
