package com.aiems.be.contracts.hospital;

import com.aiems.be.common.domain.PreKTAS;
import com.aiems.be.contracts.ai.SpecialtyConfidence;

import java.util.List;

public record HospitalRecommendRequest(
        double longitude,
        double latitude,
        PreKTAS preKtas,
        List<SpecialtyConfidence> specialtyConfidences
) {
}
