package com.aiems.be.contracts.ai;

import com.aiems.be.common.domain.Specialty;
import com.fasterxml.jackson.annotation.JsonProperty;

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
