package com.aiems.be.auth.domain;

import com.aiems.be.common.domain.BaseTimeEntity;
import com.aiems.be.common.domain.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    private Member(String loginId, String password, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.role = role;
    }
}
