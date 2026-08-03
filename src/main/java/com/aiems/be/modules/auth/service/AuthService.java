package com.aiems.be.modules.auth.service;

import com.aiems.be.common.domain.Role;
import com.aiems.be.common.exception.BusinessException;
import com.aiems.be.modules.auth.domain.Member;
import com.aiems.be.modules.auth.exception.AuthErrorCode;
import com.aiems.be.modules.auth.repository.MemberRepository;
import com.aiems.be.modules.auth.security.MemberAuthenticationToken;
import com.aiems.be.modules.auth.security.MemberDetails;
import com.aiems.be.modules.auth.token.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final AuthenticationManager authenticationManager;

    public TokenPair loginHospital(String loginId, String password) {
        return login(MemberAuthenticationToken.unauthenticated(loginId, password, Role.HOSPITAL));
    }

    public TokenPair loginControl(String loginId, String password) {
        return login(MemberAuthenticationToken.unauthenticated(loginId, password, Role.CONTROL_SYSTEM));
    }

    public TokenPair loginAmbulance(String loginId, String password, String deviceId) {
        return login(MemberAuthenticationToken.unauthenticatedAmbulance(loginId, password, deviceId));
    }


    private TokenPair login(MemberAuthenticationToken token) {
        MemberDetails principal;

        try {
            Authentication authentication = authenticationManager.authenticate(token);
            principal = (MemberDetails) authentication.getPrincipal();
        } catch (AuthenticationException e) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        return authTokenService.issue(principal.getMemberId(), principal.getRole());
    }

    public TokenPair reissue(String rawRefreshToken) {
        Long memberId = authTokenService.consume(rawRefreshToken)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.MEMBER_NOT_FOUND));

        return authTokenService.issue(member.getId(), Role.valueOf(member.getRole()));
    }

    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null) {
            authTokenService.revoke(rawRefreshToken);
        }
    }
}
