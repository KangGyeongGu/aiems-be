package com.aiems.be.auth.security;

import com.aiems.be.common.domain.Role;
import com.aiems.be.auth.domain.Member;
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

    private MemberDetails(Long memberId, String loginId, String password, Role role) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
    }

    public static MemberDetails from(Member member) {
        return new MemberDetails(
                member.getId(),
                member.getLoginId(),
                member.getPassword(),
                member.getRole()
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
