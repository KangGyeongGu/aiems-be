package com.aiems.be.modules.patient.service.result;

import com.aiems.be.modules.hospital.domain.Specialty;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SpecialtyConfidence(
        @JsonProperty("specialty")
        Specialty specialty,

        @JsonProperty("confidence")
        Float confidence
) implements Comparable<SpecialtyConfidence> {

    @Override
    public int compareTo(SpecialtyConfidence o) {
        return Float.compare(confidence, o.confidence);
    }
}
