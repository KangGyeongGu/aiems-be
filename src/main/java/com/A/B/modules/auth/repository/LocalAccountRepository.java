package com.A.B.modules.auth.repository;

import com.A.B.modules.auth.domain.LocalAccount;
import com.A.B.modules.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long> {

    Optional<LocalAccount> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    void deleteByMember(Member member);
}
