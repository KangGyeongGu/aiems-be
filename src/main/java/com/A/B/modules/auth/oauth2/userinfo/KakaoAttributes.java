package com.A.B.modules.auth.oauth2.userinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record KakaoAttributes(
        Long id,
        @JsonProperty("kakao_account") Account account
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Account(
            String email,
            Profile profile
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        record Profile(
                String nickname
        ) {
        }
    }
}
