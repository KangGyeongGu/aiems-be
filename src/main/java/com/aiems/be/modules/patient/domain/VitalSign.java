package com.aiems.be.modules.patient.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class VitalSign {

    private Integer minBloodPressure;

    private Integer maxBloodPressure;

    private Integer pulse;

    private Integer respiratoryRate;

    private Double temperature;

    @Builder
    private VitalSign(
            Integer minBloodPressure,
            Integer maxBloodPressure,
            Integer pulse,
            Integer respiratoryRate,
            Double temperature
    ) {
        this.minBloodPressure = minBloodPressure;
        this.maxBloodPressure = maxBloodPressure;
        this.pulse = pulse;
        this.respiratoryRate = respiratoryRate;
        this.temperature = temperature;
    }
}
