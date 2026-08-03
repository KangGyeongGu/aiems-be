package com.aiems.be.modules.auth.domain;

import com.aiems.be.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(String email, String username, Role role, MemberStatus status) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    public static Member create(String email, String username) {
        return Member.builder()
                .email(email)
                .username(username)
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.email = null;
    }
}
