package com.aiems.be.modules.patient.service.result;

import com.aiems.be.modules.patient.domain.PreKTAS;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

@Builder
public record PatientAnalysisResult(
        @JsonProperty("specialtyConfidences")
        List<SpecialtyConfidence> specialtyConfidences,

        @JsonProperty("preKTAS")
        PreKTAS preKTAS
) {
    public PatientAnalysisResult {
        Objects.requireNonNull(specialtyConfidences);
        Objects.requireNonNull(preKTAS);

        specialtyConfidences = specialtyConfidences.stream().distinct().toList();
    }

}
