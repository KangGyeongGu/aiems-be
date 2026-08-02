package com.A.B.modules.auth.oauth2.userinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record GithubAttributes(
        Long id,
        String login,
        String name,
        String email
) {
}
