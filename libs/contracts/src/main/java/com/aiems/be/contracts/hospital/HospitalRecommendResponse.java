package com.aiems.be.contracts.hospital;

import com.aiems.be.contracts.bed.BedInfo;

public record HospitalRecommendResponse(
        Long hospitalId,
        String name,
        String hpid,
        Double distance,
        double score,
        BedInfo bedInfo
) {
}
