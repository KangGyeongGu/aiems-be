package com.aiems.be.modules.auth.security;

import com.aiems.be.common.domain.Role;
import com.aiems.be.modules.auth.domain.Ambulance;
import com.aiems.be.modules.auth.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MemberDetails implements UserDetails {

    @Getter private final Long memberId;
    private final String loginId;
    private final String password;
    @Getter private final Role role;
    @Getter private final String deviceId;

    private MemberDetails(Long memberId, String loginId, String password, Role role, String deviceId) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.deviceId = deviceId;
    }

    public static MemberDetails from(Member member) {
        String deviceId = member instanceof Ambulance ambulance ? ambulance.getDeviceId() : null;

        return new MemberDetails(
                member.getId(),
                member.getLoginId(),
                member.getPassword(),
                Role.valueOf(member.getRole()),
                deviceId
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.authority()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() { return loginId; }
}
