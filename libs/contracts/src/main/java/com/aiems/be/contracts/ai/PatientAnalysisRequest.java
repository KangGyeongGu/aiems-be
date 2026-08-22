package com.aiems.be.contracts.ai;

import com.aiems.be.common.domain.Gender;
import com.aiems.be.common.domain.VitalSign;

import java.util.List;
import java.util.Objects;

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
