package com.aiems.be.hospital.service.policy;

import com.aiems.be.hospital.domain.Hospital;
import com.aiems.be.common.domain.Specialty;
import com.aiems.be.contracts.ai.SpecialtyConfidence;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public interface HospitalScoringPolicy {

    double calculateScore(ScoreContext context);

    @Builder
    @Getter
    class ScoreContext {
        private Hospital hospital;
        private List<SpecialtyConfidence> requiredSpecialties;
        private List<Specialty> hospitalSpecialties;
        private Double distance;
    }
}