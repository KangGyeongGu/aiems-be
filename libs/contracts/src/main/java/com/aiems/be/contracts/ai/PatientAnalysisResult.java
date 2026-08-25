package com.aiems.be.contracts.ai;

import com.aiems.be.common.domain.PreKTAS;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

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
