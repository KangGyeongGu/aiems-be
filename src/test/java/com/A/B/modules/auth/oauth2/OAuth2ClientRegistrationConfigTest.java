package com.A.B.modules.auth.oauth2;

import com.A.B.modules.auth.oauth2.config.OAuth2ClientRegistrationConfig;
import com.A.B.modules.auth.oauth2.config.OAuth2Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OAuth2 클라이언트 등록 구성 단위 테스트")
class OAuth2ClientRegistrationConfigTest {

    @EnableConfigurationProperties(OAuth2Properties.class)
    @Configuration
    static class Config {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(Config.class, OAuth2ClientRegistrationConfig.class);

    @Test
    @DisplayName("app.oauth2 의 provider 자격증명으로 구성된 provider만 등록한다")
    void bindsProvidersFromAppOauth2() {
        runner.withPropertyValues(
                "app.auth.social.enabled=true",
                "app.oauth2.google.client-id=g-id",
                "app.oauth2.google.client-secret=g-secret",
                "app.oauth2.kakao.client-id=k-id",
                "app.oauth2.kakao.client-secret=k-secret"
        ).run(context -> {
            assertThat(context).hasSingleBean(ClientRegistrationRepository.class);
            ClientRegistrationRepository repo = context.getBean(ClientRegistrationRepository.class);

            ClientRegistration google = repo.findByRegistrationId("google");
            ClientRegistration kakao = repo.findByRegistrationId("kakao");
            assertThat(google.getClientId()).isEqualTo("g-id");
            assertThat(kakao.getClientId()).isEqualTo("k-id");
            assertThat(repo.findByRegistrationId("naver")).isNull();
        });
    }

    @Test
    @DisplayName("social 토글이 꺼져 있으면 등록 구성이 뜨지 않는다")
    void disabledWhenSocialOff() {
        runner.withPropertyValues("app.auth.social.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(ClientRegistrationRepository.class));
    }
}
