package com.aiems.be.modules.auth.security;

import com.aiems.be.modules.auth.domain.LocalAccount;
import com.aiems.be.modules.auth.domain.Member;
import com.aiems.be.modules.auth.domain.MemberStatus;
import com.aiems.be.modules.auth.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LocalUserDetails implements UserDetails {

    @Getter private final Long memberId;
    private final String loginId;
    private final String password;
    @Getter private final Role role;
    private final boolean active;

    private LocalUserDetails(Long memberId, String loginId, String password, Role role, boolean active) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public static LocalUserDetails from(LocalAccount account) {
        Member member = account.getMember();
        return new LocalUserDetails(
                member.getId(),
                account.getLoginId(),
                account.getPassword(),
                member.getRole(),
                member.getStatus() == MemberStatus.ACTIVE
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
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
