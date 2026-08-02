package com.A.B.modules.auth.oauth2.service;

import com.A.B.modules.auth.domain.AuthProvider;
import com.A.B.modules.auth.domain.Member;
import com.A.B.modules.auth.oauth2.userinfo.OAuth2UserInfo;
import com.A.B.modules.auth.oauth2.userinfo.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.social.enabled", havingValue = "true")
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String MEMBER_ID_ATTRIBUTE = "memberId";

    private final OAuth2MemberService oAuth2MemberService;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.from(registrationId);
        OAuth2UserInfo userInfo = oAuth2UserInfoFactory.create(provider, oAuth2User.getAttributes());

        Member member = oAuth2MemberService.findOrCreate(provider, userInfo);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put(MEMBER_ID_ATTRIBUTE, member.getId());
        attributes.put("email", member.getEmail());
        attributes.put("role", member.getRole().name());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(member.getRole().authority())),
                attributes,
                MEMBER_ID_ATTRIBUTE
        );
    }
}
