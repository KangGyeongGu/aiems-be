package com.aiems.be.modules.patient.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreKTAS {

    LEVEL_1("소생", 3),
    LEVEL_2("응급", 3),
    LEVEL_3("준응급", 3),
    LEVEL_4("경증", 1),
    LEVEL_5("비응급", 1);

    private final String description;
    private final int hospitalLevel;

    public boolean isEmergency() {
        return this == LEVEL_1 || this == LEVEL_2 || this == LEVEL_3;
    }
}
