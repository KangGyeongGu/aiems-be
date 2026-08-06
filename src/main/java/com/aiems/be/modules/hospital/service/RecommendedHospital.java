package com.aiems.be.modules.hospital.service;

import com.aiems.be.modules.auth.domain.Hospital;
import com.aiems.be.modules.hospital.bed.service.BedInfo;

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
