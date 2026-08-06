package com.aiems.be.modules.hospital.service.command;

import com.aiems.be.common.web.response.LocationResponse;
import com.aiems.be.modules.hospital.domain.Specialty;
import com.aiems.be.modules.patient.domain.PreKTAS;
import com.aiems.be.modules.patient.service.result.PatientAnalysisResult;
import com.aiems.be.modules.patient.service.result.SpecialtyConfidence;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

@Builder
public record HospitalRecommendCommand(
        List<SpecialtyConfidence> specialtyConfidences,
        PreKTAS preKTAS,
        double latitude,                // 환자의 위도
        double longitude,               // 환자의 경도
        Integer radius                  // 검색 반경 (단위: m)
) {

    public HospitalRecommendCommand {
        Objects.requireNonNull(preKTAS, "PreKTAS는 null일 수 없습니다.");
        Objects.requireNonNull(specialtyConfidences, "진료과목 신뢰도 리스트는 null일 수 없습니다.");

        if (preKTAS.isEmergency()) {
            radius = 10_000; // 응급 환자의 경우 반경 10km
        } else {
            radius = 2_000; // 비응급 환자의 경우 최대 반경 2km
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도(latitude)는 -90° 이상 90° 이하이어야 합니다: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도(longitude)는 -180° 이상 180° 이하이어야 합니다: " + longitude);
        }
    }

    public static HospitalRecommendCommand from(LocationResponse location, PatientAnalysisResult analysisResult) {
        return HospitalRecommendCommand.builder()
                .specialtyConfidences(analysisResult.specialtyConfidences())
                .preKTAS(analysisResult.preKTAS())
                .longitude(location.lon())
                .latitude(location.lat())
                .build();
    }

    public Specialty getTopSpecialty() {
        return specialtyConfidences.stream()
                .max(SpecialtyConfidence::compareTo)
                .orElseThrow(() -> new IllegalStateException("진료과목 신뢰도 리스트가 비어있습니다."))
                .specialty();
    }

}
