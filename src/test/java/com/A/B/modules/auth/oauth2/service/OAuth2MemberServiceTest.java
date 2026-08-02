package com.A.B.modules.auth.oauth2.service;

import com.A.B.modules.auth.domain.AuthProvider;
import com.A.B.modules.auth.domain.Member;
import com.A.B.modules.auth.domain.SocialAccount;
import com.A.B.modules.auth.oauth2.userinfo.OAuth2UserInfo;
import com.A.B.modules.auth.repository.MemberRepository;
import com.A.B.modules.auth.repository.SocialAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2 회원 조회·생성 서비스 단위 테스트")
class OAuth2MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private SocialAccountRepository socialAccountRepository;
    @Mock
    private OAuth2UserInfo userInfo;

    @InjectMocks
    private OAuth2MemberService oAuth2MemberService;

    @Test
    @DisplayName("이미 연동된 소셜 계정은 기존 회원으로 로그인시킨다")
    void findOrCreate_withExistingSocialAccount_returnsLinkedMember() {
        Member member = Member.create("a@b.com", "nick");
        given(userInfo.getProviderId()).willReturn("pid");
        given(socialAccountRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, "pid"))
                .willReturn(Optional.of(SocialAccount.create(member, AuthProvider.GOOGLE, "pid")));
        given(memberRepository.findById(any())).willReturn(Optional.of(member));

        Member result = oAuth2MemberService.findOrCreate(AuthProvider.GOOGLE, userInfo);

        assertThat(result).isSameAs(member);
        then(socialAccountRepository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    @DisplayName("처음 보는 소셜 계정은 회원으로 새로 등록한다")
    void findOrCreate_withNewSocial_createsMemberAndSocialAccount() {
        given(userInfo.getProviderId()).willReturn("pid");
        given(userInfo.getEmail()).willReturn("new@b.com");
        given(userInfo.getNickname()).willReturn("newbie");
        given(socialAccountRepository.findByProviderAndProviderId(any(), any())).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

        Member result = oAuth2MemberService.findOrCreate(AuthProvider.KAKAO, userInfo);

        assertThat(result.getEmail()).isEqualTo("new@b.com");
        assertThat(result.getUsername()).isEqualTo("newbie");
        then(socialAccountRepository).should().save(any(SocialAccount.class));
    }

    @Test
    @DisplayName("이메일을 제공하지 않는 소셜도 가입시킨다")
    void findOrCreate_withNewSocialAndNoEmail_stillCreatesMember() {
        given(userInfo.getProviderId()).willReturn("pid");
        given(userInfo.getEmail()).willReturn(null);
        given(socialAccountRepository.findByProviderAndProviderId(any(), any())).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

        Member result = oAuth2MemberService.findOrCreate(AuthProvider.KAKAO, userInfo);

        assertThat(result.getEmail()).isNull();
        then(socialAccountRepository).should().save(any(SocialAccount.class));
    }
}
