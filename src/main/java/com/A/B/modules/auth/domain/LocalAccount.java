package com.A.B.modules.auth.domain;

import com.A.B.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "local_accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalAccount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Builder(access = AccessLevel.PRIVATE)
    private LocalAccount(Member member, String loginId, String password) {
        this.member = member;
        this.loginId = loginId;
        this.password = password;
    }

    public static LocalAccount create(Member member, String loginId, String encodedPassword) {
        return LocalAccount.builder()
                .member(member)
                .loginId(loginId)
                .password(encodedPassword)
                .build();
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
