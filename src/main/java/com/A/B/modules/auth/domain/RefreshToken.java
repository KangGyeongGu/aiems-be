package com.A.B.modules.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_member", columnList = "member_id"),
                @Index(name = "idx_refresh_token_expires_at", columnList = "expires_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private RefreshToken(Long memberId, String tokenHash, Instant expiresAt) {
        this.memberId = memberId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(Long memberId, String tokenHash, Instant expiresAt) {
        return RefreshToken.builder()
                .memberId(memberId)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .build();
    }

    public boolean isUsed() {
        return usedAt != null;
    }
}
