package com.aiems.be.modules.hospital.service.result;

import com.aiems.be.modules.hospital.domain.Hospital;
import lombok.Builder;

@Builder
public record ScoredHospital(
        Hospital hospital,
        Double distance,
        double score,
        BedInfo bedInfo
) implements Comparable<ScoredHospital> {

    @Override
    public int compareTo(ScoredHospital o) {
        return Double.compare(o.score, score);
    }
}