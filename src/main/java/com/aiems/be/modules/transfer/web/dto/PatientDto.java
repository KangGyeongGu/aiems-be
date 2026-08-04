package com.aiems.be.modules.transfer.web.dto;

import lombok.Builder;

@Builder
public record PatientDto(
        Long id,
        AmbulanceDto ambulance,
        String name,
        int age,
        String gender,
        VitalSignDto vitalSign,
        String symptoms,
        String preKtas,
        String firstAid,
        String cause,
        String underlyingDisease
) {
}
