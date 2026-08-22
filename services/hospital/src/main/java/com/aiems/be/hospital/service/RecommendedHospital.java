package com.aiems.be.hospital.service;

import com.aiems.be.hospital.domain.Hospital;
import com.aiems.be.contracts.bed.BedInfo;

public record RecommendedHospital(
        Hospital hospital,
        Double distance,
        double score,
        BedInfo bedInfo
) implements Comparable<RecommendedHospital> {

    @Override
    public int compareTo(RecommendedHospital o) {
        return Double.compare(o.score, score);
    }
}
