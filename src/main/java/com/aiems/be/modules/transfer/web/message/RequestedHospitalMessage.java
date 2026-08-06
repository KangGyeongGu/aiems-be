package com.aiems.be.modules.transfer.web.message;

import com.aiems.be.modules.hospital.service.result.ScoredHospital;
import com.aiems.be.modules.hospital.web.response.HospitalResponse;
import lombok.Builder;

@Builder
public record RequestedHospitalMessage(
        HospitalResponse hospital,
        BedInfoMessage bedInfo,
        Double score,
        Double distance
) {

    public static RequestedHospitalMessage from(ScoredHospital scoredHospital) {
        return RequestedHospitalMessage.builder()
                .hospital(HospitalResponse.from(scoredHospital.hospital()))
                .score(scoredHospital.score())
                .distance(scoredHospital.distance())
                .bedInfo(BedInfoMessage.from(scoredHospital.bedInfo()))
                .build();
    }

}