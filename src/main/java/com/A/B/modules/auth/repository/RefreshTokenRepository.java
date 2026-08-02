package com.A.B.modules.auth.repository;

import com.A.B.modules.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.usedAt = :now WHERE r.tokenHash = :tokenHash AND r.usedAt IS NULL")
    int markUsed(@Param("tokenHash") String tokenHash, @Param("now")Instant now);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.tokenHash = :tokenHash")
    int deleteByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.memberId = :memberId")
    int deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :time")
    int deleteByExpiresAtBefore(@Param("time") Instant time);
}
