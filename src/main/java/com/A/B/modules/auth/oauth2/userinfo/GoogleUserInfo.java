package com.A.B.modules.auth.oauth2.userinfo;

record GoogleUserInfo(GoogleAttributes attributes) implements OAuth2UserInfo {

    @Override
    public String getProviderId() {
        return attributes().sub();
    }

    @Override
    public String getEmail() {
        return attributes.email();
    }

    @Override
    public String getNickname() {
        return attributes.name();
    }
}
