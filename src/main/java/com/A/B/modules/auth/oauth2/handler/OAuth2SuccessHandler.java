package com.A.B.modules.auth.oauth2.handler;

import com.A.B.common.exception.BusinessException;
import com.A.B.modules.auth.domain.Member;
import com.A.B.modules.auth.exception.AuthErrorCode;
import com.A.B.modules.auth.oauth2.config.OAuth2Properties;
import com.A.B.modules.auth.repository.MemberRepository;
import com.A.B.modules.auth.service.AuthTokenService;
import com.A.B.modules.auth.token.TokenPair;
import com.A.B.modules.auth.web.RefreshTokenCookieManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.social.enabled", havingValue = "true")
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String MEMBER_ID_ATTRIBUTE = "memberId";

    private final AuthTokenService authTokenService;
    private final MemberRepository memberRepository;
    private final RefreshTokenCookieManager refreshTokenCookieManager;
    private final OAuth2Properties oAuth2Properties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        Long memberId = Long.valueOf(String.valueOf(principal.getAttributes().get(MEMBER_ID_ATTRIBUTE)));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.MEMBER_NOT_FOUND));

        TokenPair tokens = authTokenService.issue(member.getId(), member.getRole());

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshTokenCookieManager.create(tokens.refreshToken()).toString()
        );

        String targetUrl = UriComponentsBuilder.fromUriString(oAuth2Properties.successRedirectUri())
                .fragment("accessToken=" + tokens.accessToken())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
