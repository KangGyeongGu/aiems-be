package com.aiems.be.modules.hospital.service.result;

import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Builder
public record HospitalRecommendResult(
        List<ScoredHospital> hospitals
) {

    public HospitalRecommendResult {
        Objects.requireNonNull(hospitals, "병원 리스트는 null일 수 없습니다.");
        Collections.sort(hospitals);
    }
}