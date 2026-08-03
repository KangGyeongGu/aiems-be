package com.aiems.be.modules.auth.repository;

import com.aiems.be.modules.auth.domain.LocalAccount;
import com.aiems.be.modules.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long> {

    Optional<LocalAccount> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    void deleteByMember(Member member);
}
