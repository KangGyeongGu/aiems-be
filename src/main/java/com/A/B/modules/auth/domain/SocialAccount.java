package com.A.B.modules.auth.domain;

import com.A.B.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "social_accounts",
        uniqueConstraints = @UniqueConstraint(name = "uk_social_provider", columnNames = {"provider", "provider_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Builder(access = AccessLevel.PRIVATE)
    private SocialAccount(Member member, AuthProvider provider, String providerId) {
        this.member = member;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static SocialAccount create(Member member, AuthProvider provider, String providerId) {
        return SocialAccount.builder()
                .member(member)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
