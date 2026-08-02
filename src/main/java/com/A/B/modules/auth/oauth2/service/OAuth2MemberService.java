package com.A.B.modules.auth.oauth2.service;

import com.A.B.common.exception.BusinessException;
import com.A.B.modules.auth.domain.AuthProvider;
import com.A.B.modules.auth.domain.Member;
import com.A.B.modules.auth.domain.MemberStatus;
import com.A.B.modules.auth.domain.SocialAccount;
import com.A.B.modules.auth.exception.AuthErrorCode;
import com.A.B.modules.auth.oauth2.userinfo.OAuth2UserInfo;
import com.A.B.modules.auth.repository.MemberRepository;
import com.A.B.modules.auth.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.social.enabled", havingValue = "true")
public class OAuth2MemberService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    @Transactional
    public Member findOrCreate(AuthProvider provider, OAuth2UserInfo userInfo) {
        String providerId = userInfo.getProviderId();
        if (providerId == null || providerId.isBlank()) {
            throw new BusinessException(AuthErrorCode.INVALID_OAUTH2_USER);
        }

        return socialAccountRepository.findByProviderAndProviderId(provider, providerId)
                .map(socialAccount -> memberRepository.findById(socialAccount.getMember().getId())
                        .filter(member -> member.getStatus() == MemberStatus.ACTIVE)
                        .orElseThrow(() -> new BusinessException(AuthErrorCode.MEMBER_NOT_FOUND)))
                .orElseGet(() -> register(provider, userInfo));
    }

    private Member register(AuthProvider provider, OAuth2UserInfo userInfo) {
        Member member = memberRepository.save(Member.create(userInfo.getEmail(), resolveUsername(userInfo)));
        socialAccountRepository.save(SocialAccount.create(member, provider, userInfo.getProviderId()));
        return member;
    }

    private String resolveUsername(OAuth2UserInfo userInfo) {
        return userInfo.getNickname() != null ? userInfo.getNickname() : "user";
    }
}
