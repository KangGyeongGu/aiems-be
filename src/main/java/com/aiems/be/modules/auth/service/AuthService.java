package com.aiems.be.modules.auth.service;

import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.modules.auth.domain.LocalAccount;
import com.aiems.be.modules.auth.domain.Member;
import com.aiems.be.modules.auth.domain.MemberStatus;
import com.aiems.be.modules.auth.exception.AuthErrorCode;
import com.aiems.be.modules.auth.repository.LocalAccountRepository;
import com.aiems.be.modules.auth.repository.MemberRepository;
import com.aiems.be.modules.auth.security.LocalUserDetails;
import com.aiems.be.modules.auth.token.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final LocalAccountRepository localAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Member signup(String loginId, String password, String email, String username) {
        if (localAccountRepository.existsByLoginId(loginId)) {
            throw new BusinessException(AuthErrorCode.LOGIN_ID_ALREADY_EXISTS);
        }

        Member member = memberRepository.save(Member.create(email, username));
        try {
            localAccountRepository.save(LocalAccount.create(member, loginId, passwordEncoder.encode(password)));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(AuthErrorCode.LOGIN_ID_ALREADY_EXISTS);
        }

        return member;
    }

    @Transactional
    public TokenPair login(String loginId, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginId, password));
            LocalUserDetails principal = (LocalUserDetails) authentication.getPrincipal();
            return authTokenService.issue(principal.getMemberId(), principal.getRole());
        } catch (AuthenticationException e) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }
    }

    public TokenPair reissue(String rawRefreshToken) {
        Long memberId = authTokenService.consume(rawRefreshToken)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            authTokenService.revokeAll(memberId);
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        return authTokenService.issue(member.getId(), member.getRole());
    }

    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null) {
            authTokenService.revoke(rawRefreshToken);
        }
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.MEMBER_NOT_FOUND));

        localAccountRepository.deleteByMember(member);
        member.withdraw();
        authTokenService.revokeAll(memberId);
    }
}
