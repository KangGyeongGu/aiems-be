package com.aiems.be.modules.transfer.messaging.contract;

import com.aiems.be.modules.transfer.domain.Gender;
import com.aiems.be.modules.transfer.domain.VitalSign;

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
