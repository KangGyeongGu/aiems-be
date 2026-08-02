package com.A.B.modules.auth.oauth2.userinfo;

record KakaoUserInfo(KakaoAttributes attributes) implements OAuth2UserInfo {

    @Override
    public String getProviderId() {
        return attributes.id() != null ? String.valueOf(attributes.id()) : null;
    }

    @Override
    public String getEmail() {
        return attributes.account() != null ? attributes.account().email() : null;
    }

    @Override
    public String getNickname() {
        if (attributes.account() == null || attributes.account().profile() == null) {
            return null;
        }
        return attributes.account().profile().nickname();
    }
}
