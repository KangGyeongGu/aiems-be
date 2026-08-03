package com.aiems.be.modules.auth.repository;

import com.aiems.be.modules.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
