package com.A.B.modules.auth.repository;

import com.A.B.modules.auth.domain.Member;
import com.A.B.modules.auth.domain.RefreshToken;
import com.A.B.support.RepositoryTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RefreshToken 리포지토리 슬라이스 테스트")
class RefreshTokenRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;
    private Long otherMemberId;

    @BeforeEach
    void seedMembers() {
        memberId = memberRepository.save(Member.create(null, "member")).getId();
        otherMemberId = memberRepository.save(Member.create(null, "other")).getId();
    }

    private Instant future() {
        return Instant.now().plusSeconds(3600);
    }

    @Test
    @DisplayName("토큰 해시로 저장하고 다시 찾을 수 있다")
    void saveAndFindByTokenHash() {
        refreshTokenRepository.save(RefreshToken.create(memberId, "hash-1", future()));

        assertThat(refreshTokenRepository.findByTokenHash("hash-1")).isPresent();
        assertThat(refreshTokenRepository.findByTokenHash("missing")).isEmpty();
    }

    @Test
    @DisplayName("토큰 해시로 개별 삭제한다")
    void deleteByTokenHash() {
        refreshTokenRepository.save(RefreshToken.create(memberId, "hash-1", future()));

        refreshTokenRepository.deleteByTokenHash("hash-1");

        assertThat(refreshTokenRepository.findByTokenHash("hash-1")).isEmpty();
    }

    @Test
    @DisplayName("회원 단위 삭제는 다른 회원 토큰을 건드리지 않는다")
    void deleteByMemberId_removesOnlyThatMembersTokens() {
        refreshTokenRepository.save(RefreshToken.create(memberId, "h1", future()));
        refreshTokenRepository.save(RefreshToken.create(memberId, "h2", future()));
        refreshTokenRepository.save(RefreshToken.create(otherMemberId, "h3", future()));

        refreshTokenRepository.deleteByMemberId(memberId);

        assertThat(refreshTokenRepository.findByTokenHash("h1")).isEmpty();
        assertThat(refreshTokenRepository.findByTokenHash("h2")).isEmpty();
        assertThat(refreshTokenRepository.findByTokenHash("h3")).isPresent();
    }

    @Test
    @DisplayName("토큰 소비는 한 번만 성공한다")
    void markUsed_secondCallReturnsZero() {
        refreshTokenRepository.save(RefreshToken.create(memberId, "hash-1", future()));

        assertThat(refreshTokenRepository.markUsed("hash-1", Instant.now())).isEqualTo(1);
        assertThat(refreshTokenRepository.markUsed("hash-1", Instant.now())).isZero();
    }

    @Test
    @DisplayName("만료된 토큰만 삭제되어야 한다")
    void deleteByExpiresAtBefore_removesExpiredOnly() {
        refreshTokenRepository.save(RefreshToken.create(memberId, "expired", Instant.now().minusSeconds(10)));
        refreshTokenRepository.save(RefreshToken.create(memberId, "fresh", future()));

        long removed = refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());

        assertThat(removed).isEqualTo(1);
        assertThat(refreshTokenRepository.findByTokenHash("expired")).isEmpty();
        assertThat(refreshTokenRepository.findByTokenHash("fresh")).isPresent();
    }
}
