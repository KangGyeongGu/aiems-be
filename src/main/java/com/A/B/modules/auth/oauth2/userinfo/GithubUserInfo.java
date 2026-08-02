package com.A.B.modules.auth.oauth2.userinfo;

record GithubUserInfo(GithubAttributes attributes) implements OAuth2UserInfo {

    @Override
    public String getProviderId() {
        return attributes.id() != null ? String.valueOf(attributes.id()) : null;
    }

    @Override
    public String getEmail() {
        return attributes.email();
    }

    @Override
    public String getNickname() {
        return attributes.name() != null ? attributes.name() : attributes.login();
    }
}
