package com.aiems.be.transfer.client;

import com.aiems.be.common.domain.PreKTAS;
import com.aiems.be.contracts.bed.BedInfo;
import com.aiems.be.contracts.ai.SpecialtyConfidence;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalRecommendClient {

    private static final String RESILIENCE_NAME = "hospitalRecommend";

    private final HospitalRecommendApi hospitalRecommendApi;

    @CircuitBreaker(name = RESILIENCE_NAME, fallbackMethod = "recommendFallback")
    @Bulkhead(name = RESILIENCE_NAME)
    public List<RecommendedHospital> recommend(double longitude, double latitude,
                                               PreKTAS preKtas, List<SpecialtyConfidence> specialtyConfidences) {
        List<RecommendedHospital> result = hospitalRecommendApi.recommend(
                new Request(longitude, latitude, preKtas, specialtyConfidences));
        return result != null ? result : List.of();
    }

    private List<RecommendedHospital> recommendFallback(double longitude, double latitude,
                                                        PreKTAS preKtas, List<SpecialtyConfidence> specialtyConfidences,
                                                        Throwable t) {
        log.warn("병원 추천 호출 실패, 폴백(빈 목록) 반환. cause={}", t.toString());
        return List.of();
    }

    public record Request(
            double longitude,
            double latitude,
            PreKTAS preKtas,
            List<SpecialtyConfidence> specialtyConfidences
    ) {
    }

    public record RecommendedHospital(
            Long hospitalId,
            String name,
            String hpid,
            Double distance,
            double score,
            BedInfo bedInfo
    ) {
    }
}
