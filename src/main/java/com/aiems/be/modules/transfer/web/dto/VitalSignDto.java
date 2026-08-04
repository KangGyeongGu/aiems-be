package com.aiems.be.modules.transfer.web.dto;

import lombok.Builder;

@Builder
public record VitalSignDto(
        Integer minBloodPressure,
        Integer maxBloodPressure,
        Integer pulse,
        Integer respiratoryRate,
        Double temperature
) {
}
