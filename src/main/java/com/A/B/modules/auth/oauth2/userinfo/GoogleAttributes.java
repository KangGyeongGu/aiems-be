package com.A.B.modules.auth.oauth2.userinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record GoogleAttributes(
        String sub,
        String email,
        String name
) {
}
