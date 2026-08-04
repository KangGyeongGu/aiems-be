package com.aiems.be.modules.patient.service.request;

import com.aiems.be.modules.patient.domain.Gender;
import com.aiems.be.modules.patient.domain.VitalSign;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

@Builder
public record PatientAnalysisRequest(
        String symptoms,
        List<String> underlyingDiseases,
        Gender gender,
        int age,
        VitalSign vitalSign,
        String info
) {
    public PatientAnalysisRequest {
        Objects.requireNonNull(symptoms);
        Objects.requireNonNull(underlyingDiseases);
        Objects.requireNonNull(gender);
        Objects.requireNonNull(vitalSign);
        Objects.requireNonNull(info);
    }
}
