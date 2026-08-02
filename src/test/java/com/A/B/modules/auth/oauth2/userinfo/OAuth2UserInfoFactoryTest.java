package com.A.B.modules.auth.oauth2.userinfo;

import com.A.B.modules.auth.domain.AuthProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OAuth2 사용자 정보 파서 팩토리 단위 테스트")
class OAuth2UserInfoFactoryTest {

    private final OAuth2UserInfoFactory factory = new OAuth2UserInfoFactory(new ObjectMapper());

    @Test
    @DisplayName("구글 응답에서 사용자 정보를 추출한다")
    void create_google_parsesFlatAttributes() {
        Map<String, Object> attributes = Map.of(
                "sub", "g-123",
                "email", "g@example.com",
                "name", "Google User"
        );

        OAuth2UserInfo info = factory.create(AuthProvider.GOOGLE, attributes);

        assertThat(info.getProviderId()).isEqualTo("g-123");
        assertThat(info.getEmail()).isEqualTo("g@example.com");
        assertThat(info.getNickname()).isEqualTo("Google User");
    }

    @Test
    @DisplayName("카카오의 중첩 응답에서 사용자 정보를 추출한다")
    void create_kakao_parsesNestedAttributes() {
        Map<String, Object> attributes = Map.of(
                "id", 12345L,
                "kakao_account", Map.of(
                        "email", "k@example.com",
                        "profile", Map.of("nickname", "Kakao User")
                )
        );

        OAuth2UserInfo info = factory.create(AuthProvider.KAKAO, attributes);

        assertThat(info.getProviderId()).isEqualTo("12345");
        assertThat(info.getEmail()).isEqualTo("k@example.com");
        assertThat(info.getNickname()).isEqualTo("Kakao User");
    }

    @Test
    @DisplayName("네이버 응답에서 사용자 정보를 추출한다")
    void create_naver_parsesResponseAttributes() {
        Map<String, Object> attributes = Map.of(
                "response", Map.of(
                        "id", "n-1",
                        "email", "n@example.com",
                        "nickname", "Naver User"
                )
        );

        OAuth2UserInfo info = factory.create(AuthProvider.NAVER, attributes);

        assertThat(info.getProviderId()).isEqualTo("n-1");
        assertThat(info.getEmail()).isEqualTo("n@example.com");
        assertThat(info.getNickname()).isEqualTo("Naver User");
    }

    @Test
    @DisplayName("깃허브 응답에서 사용자 정보를 추출한다")
    void create_github_parsesAttributes() {
        Map<String, Object> attributes = Map.of(
                "id", 98765,
                "login", "octocat",
                "name", "Github User",
                "email", "gh@example.com"
        );

        OAuth2UserInfo info = factory.create(AuthProvider.GITHUB, attributes);

        assertThat(info.getProviderId()).isEqualTo("98765");
        assertThat(info.getEmail()).isEqualTo("gh@example.com");
        assertThat(info.getNickname()).isEqualTo("Github User");
    }

    @Test
    @DisplayName("깃허브는 이름이 없으면 로그인 아이디를 닉네임으로 쓴다")
    void create_github_fallsBackToLoginWhenNameMissing() {
        Map<String, Object> attributes = Map.of(
                "id", 98765,
                "login", "octocat",
                "email", "gh@example.com"
        );

        OAuth2UserInfo info = factory.create(AuthProvider.GITHUB, attributes);

        assertThat(info.getNickname()).isEqualTo("octocat");
    }
}