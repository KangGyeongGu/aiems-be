package com.A.B.modules.auth.service;

import com.A.B.common.exception.BusinessException;
import com.A.B.modules.auth.domain.LocalAccount;
import com.A.B.modules.auth.domain.Member;
import com.A.B.modules.auth.domain.MemberStatus;
import com.A.B.modules.auth.exception.AuthErrorCode;
import com.A.B.modules.auth.repository.LocalAccountRepository;
import com.A.B.modules.auth.repository.MemberRepository;
import com.A.B.modules.auth.repository.SocialAccountRepository;
import com.A.B.modules.auth.security.LocalUserDetails;
import com.A.B.modules.auth.token.TokenPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LocalAccountRepository localAccountRepository;
    @Mock
    private SocialAccountRepository socialAccountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthTokenService authTokenService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입은 회원과 로컬 자격증명을 함께 만든다")
    void signup_withNewLoginId_createsMemberAndLocalAccount() {
        given(localAccountRepository.existsByLoginId("gildong")).willReturn(false);
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(passwordEncoder.encode("password123")).willReturn("encoded");

        Member member = authService.signup("gildong", "password123", "a@b.com", "nick");

        assertThat(member.getEmail()).isEqualTo("a@b.com");
        assertThat(member.getUsername()).isEqualTo("nick");
        then(localAccountRepository).should().save(any(LocalAccount.class));
    }

    @Test
    @DisplayName("중복된 loginId 가입은 거부한다")
    void signup_withDuplicateLoginId_throws() {
        given(localAccountRepository.existsByLoginId("gildong")).willReturn(true);

        BusinessException ex = catchThrowableOfType(
                BusinessException.class,
                () -> authService.signup("gildong", "password123", "a@b.com", "nick")
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorCode.LOGIN_ID_ALREADY_EXISTS);
        then(memberRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("자격증명이 맞으면 토큰을 발급한다")
    void login_withValidCredentials_issuesTokens() {
        LocalUserDetails principal = LocalUserDetails.from(
                LocalAccount.create(Member.create("a@b.com", "nick"), "gildong", "encoded"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        given(authenticationManager.authenticate(any())).willReturn(authentication);
        TokenPair pair = new TokenPair("access", "refresh", Instant.now().plusSeconds(60));
        given(authTokenService.issue(any(), any())).willReturn(pair);

        TokenPair result = authService.login("gildong", "password123");

        assertThat(result.accessToken()).isEqualTo("access");
        then(authTokenService).should().issue(any(), any());
    }

    @Test
    @DisplayName("비밀번호가 틀리면 로그인을 거부한다")
    void login_withWrongPassword_throwsInvalidCredentials() {
        given(authenticationManager.authenticate(any()))
                .willThrow(new BadCredentialsException("bad credentials"));

        BusinessException ex = catchThrowableOfType(
                BusinessException.class,
                () -> authService.login("gildong", "wrong")
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);
        then(authTokenService).should(never()).issue(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 계정 로그인은 거부한다")
    void login_withUnknownLoginId_throwsInvalidCredentials() {
        given(authenticationManager.authenticate(any()))
                .willThrow(new UsernameNotFoundException("not found"));

        BusinessException ex = catchThrowableOfType(
                BusinessException.class,
                () -> authService.login("nobody", "password123")
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("재발급시 기존 토큰은 소비하고 새 토큰을 발급한다")
    void reissue_consumesOldTokenAndIssuesNew() {
        given(authTokenService.consume("old-refresh")).willReturn(Optional.of(1L));
        given(memberRepository.findById(1L)).willReturn(Optional.of(Member.create("a@b.com", "nick")));
        TokenPair pair = new TokenPair("new-access", "new-refresh", Instant.now().plusSeconds(60));
        given(authTokenService.issue(any(), any())).willReturn(pair);

        TokenPair result = authService.reissue("old-refresh");

        then(authTokenService).should().consume("old-refresh");
        assertThat(result.accessToken()).isEqualTo("new-access");
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰의 재발급은 거부한다")
    void reissue_withInvalidToken_throwsInvalidRefreshToken() {
        given(authTokenService.consume("bad")).willReturn(Optional.empty());

        BusinessException ex = catchThrowableOfType(
                BusinessException.class,
                () -> authService.reissue("bad")
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN);
        then(authTokenService).should(never()).issue(any(), any());
    }

    @Test
    @DisplayName("로그아웃 시 리프레시 토큰을 폐기한다")
    void logout_revokesRefreshToken() {
        authService.logout("some-refresh");

        then(authTokenService).should().revoke("some-refresh");
    }

    @Test
    @DisplayName("탈퇴 회원의 재발급은 세션 전량 폐기 후 거부한다")
    void reissue_withWithdrawnMember_revokesAllAndThrows() {
        Member member = Member.create("a@b.com", "nick");
        member.withdraw();
        given(authTokenService.consume("refresh")).willReturn(Optional.of(1L));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        BusinessException ex = catchThrowableOfType(
                BusinessException.class,
                () -> authService.reissue("refresh")
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN);
        then(authTokenService).should().revokeAll(1L);
        then(authTokenService).should(never()).issue(any(), any());
    }

    @Test
    @DisplayName("탈퇴는 계정을 지우고 회원을 비활성화하며 세션을 전량 폐기한다")
    void withdraw_deletesAccountsAndRevokesTokens() {
        Member member = Member.create("a@b.com", "nick");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        authService.withdraw(1L);

        assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
        assertThat(member.getEmail()).isNull();
        then(localAccountRepository).should().deleteByMember(member);
        then(socialAccountRepository).should().deleteByMember(member);
        then(authTokenService).should().revokeAll(1L);
    }

    @Test
    @DisplayName("이미 탈퇴한 회원의 재탈퇴는 거부한다")
    void withdraw_alreadyWithdrawn_throwsMemberNotFound() {
        Member member = Member.create("a@b.com", "nick");
        member.withdraw();
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        BusinessException ex = catchThrowableOfType(
                BusinessException.class,
                () -> authService.withdraw(1L)
        );

        assertThat(ex.getErrorCode()).isEqualTo(AuthErrorCode.MEMBER_NOT_FOUND);
        then(localAccountRepository).should(never()).deleteByMember(any());
    }
}
