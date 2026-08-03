package com.aiems.be.modules.auth.token;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenStore {

    void save(String memberId, String rawToken, Instant expiresAt);

    Optional<Long> consume(String rawToken);

    void delete(String rawToken);

    void deleteByMemberId(String memberId);
}
