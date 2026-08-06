package com.aiems.be.modules.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationStatus {
    ACTIVE("활동 중"),
    STANDBY("대기 중");

    private final String description;
    private OperationStatus nextStatus;

    static {
        ACTIVE.nextStatus = STANDBY;
        STANDBY.nextStatus = ACTIVE;
    }

}
