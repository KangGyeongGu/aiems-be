package com.A.B.modules.auth.oauth2.userinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record NaverAttributes(
        Response response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Response(
            String id,
            String email,
            String nickname
    ) {
    }
}
