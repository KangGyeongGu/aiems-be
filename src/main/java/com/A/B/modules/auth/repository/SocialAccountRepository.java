package com.A.B.modules.auth.repository;

import com.A.B.modules.auth.domain.AuthProvider;
import com.A.B.modules.auth.domain.Member;
import com.A.B.modules.auth.domain.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderId(AuthProvider provider, String providerId);

    void deleteByMember(Member member);
}
