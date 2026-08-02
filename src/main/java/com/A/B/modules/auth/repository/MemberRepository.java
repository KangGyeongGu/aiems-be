package com.A.B.modules.auth.repository;

import com.A.B.modules.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
