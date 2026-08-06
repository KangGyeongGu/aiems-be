package com.aiems.be.modules.patient.web.response;

import com.aiems.be.modules.patient.domain.VitalSign;
import lombok.Builder;

@Builder
public record VitalSignResponse(
        Integer minBloodPressure,
        Integer maxBloodPressure,
        Integer pulse,
        Integer respiratoryRate,
        Double temperature
) {
    public static VitalSignResponse from(VitalSign vitalSign) {
        return VitalSignResponse.builder()
                .minBloodPressure(vitalSign.getMinBloodPressure())
                .maxBloodPressure(vitalSign.getMaxBloodPressure())
                .pulse(vitalSign.getPulse())
                .respiratoryRate(vitalSign.getRespiratoryRate())
                .temperature(vitalSign.getTemperature())
                .build();
    }

}