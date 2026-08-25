package com.aiems.be.common.domain;

public enum Role {
    MEMBER,
    AMBULANCE,
    CONTROL_SYSTEM,
    HOSPITAL;

    public String authority() {
        return "ROLE_" + name();
    }
}
