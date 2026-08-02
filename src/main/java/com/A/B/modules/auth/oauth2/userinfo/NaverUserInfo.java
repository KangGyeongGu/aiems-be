package com.A.B.modules.auth.oauth2.userinfo;

record NaverUserInfo(NaverAttributes attributes) implements OAuth2UserInfo {

    @Override
    public String getProviderId() {
        return attributes.response() != null ? attributes.response().id() : null;
    }

    @Override
    public String getEmail() {
        return attributes.response() != null ? attributes.response().email() : null;
    }

    @Override
    public String getNickname() {
        return attributes.response() != null ? attributes.response().nickname() : null;
    }
}
