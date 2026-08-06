package com.aiems.be.modules.transfer.web.payload;

import com.aiems.be.modules.ambulance.domain.Ambulance;
import com.aiems.be.common.web.response.LocationResponse;
import com.aiems.be.modules.patient.domain.Gender;
import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.patient.domain.PreKTAS;
import com.aiems.be.modules.patient.domain.VitalSign;
import com.aiems.be.modules.patient.service.request.PatientAnalysisRequest;
import com.aiems.be.modules.patient.web.response.VitalSignResponse;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
public record PatientTransferRequestPayload(
        String name,
        Integer age,
        String gender,
        String symptoms,                    // 주요 증상
        String cause,                       // 사고 원인
        String firstAid,                    // 응급처치 내용
        List<String> underlyingDisease,     // 기저질환
        VitalSignResponse vitalSign,
        LocationResponse accidentLocation
) {

    public PatientTransferRequestPayload {
        Arrays.stream(Gender.values())
                .filter(g -> gender.equals(g.name()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[%s] 는 유효하지 않은 성별입니다.".formatted(gender)));
    }

    public PatientAnalysisRequest toAnalysisRequest() {
        return PatientAnalysisRequest.builder()
                .age(age())
                .gender(Gender.valueOf(gender()))
                .symptoms(symptoms())
                .vitalSign(VitalSign.builder()
                        .pulse(vitalSign().pulse())
                        .respiratoryRate(vitalSign().respiratoryRate())
                        .temperature(vitalSign().temperature())
                        .maxBloodPressure(vitalSign().maxBloodPressure())
                        .minBloodPressure(vitalSign().minBloodPressure())
                        .build())
                .info(cause())
                .underlyingDiseases(underlyingDisease())
                .build();
    }

    public Patient toEntity(PreKTAS preKTAS, Ambulance ambulance) {
        StringBuilder diseaseStr = new StringBuilder();
        for (String disease : underlyingDisease) {
            diseaseStr.append(disease).append(", ");
        }

        return Patient.builder()
                .name(name())
                .age(age())
                .gender(Gender.MALE)
                .symptoms(symptoms())
                .preKtas(preKTAS)
                .vitalSign(VitalSign.builder()
                        .maxBloodPressure(vitalSign().maxBloodPressure())
                        .minBloodPressure(vitalSign().minBloodPressure())
                        .temperature(vitalSign().temperature())
                        .respiratoryRate(vitalSign().respiratoryRate())
                        .pulse(vitalSign().pulse())
                        .build())
                .ambulance(ambulance)
                .location(accidentLocation.toEntity())
                .firstAid(firstAid)
                .cause(cause)
                .underlyingDisease(diseaseStr.toString())
                .build();
    }
}