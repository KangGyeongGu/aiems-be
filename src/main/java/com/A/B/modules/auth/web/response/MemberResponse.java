package com.A.B.modules.auth.web.response;

import com.A.B.modules.auth.domain.Member;

public record MemberResponse(
        Long id,
        String email,
        String username,
        String role
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getRole().name()
        );
    }
}
