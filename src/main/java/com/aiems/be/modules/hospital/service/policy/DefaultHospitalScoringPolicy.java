package com.aiems.be.modules.hospital.service.policy;

import com.aiems.be.modules.patient.service.result.SpecialtyConfidence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultHospitalScoringPolicy implements HospitalScoringPolicy {

    private static final double DISTANCE_WEIGHT = 0.7;
    private static final double SPECIALTY_WEIGHT = 0.3;

    @Override
    public double calculateScore(ScoreContext context) {
        double specialtyScore = calculateSpecialtyScore(context) * SPECIALTY_WEIGHT;
        double distanceScore = calculateDistanceScore(context) * DISTANCE_WEIGHT;
        return specialtyScore + distanceScore;
    }

    private static double calculateSpecialtyScore(ScoreContext context) {
        double score = 0.0;
        for (SpecialtyConfidence required : context.getRequiredSpecialties()) {
            if (context.getHospitalSpecialties().contains(required.specialty())) {
                score += required.confidence();
            }
        }
        return score;
    }

    private double calculateDistanceScore(ScoreContext context) {
        double score = 1 - (context.getDistance() / 100000);
        return Math.max(0, score);
    }

}