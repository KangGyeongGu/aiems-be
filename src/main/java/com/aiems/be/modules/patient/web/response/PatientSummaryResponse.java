package com.aiems.be.modules.patient.web.response;

import com.aiems.be.modules.patient.domain.Gender;
import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.patient.domain.PreKTAS;
import lombok.Builder;

@Builder
public record PatientSummaryResponse(
    Long patientId,
    String patientName,
    Integer age,
    Gender gender,
    String symptoms,
    PreKTAS preKTAS
) {
    public static PatientSummaryResponse from(Patient patient) {
        return PatientSummaryResponse.builder()
                .patientId(patient.getId())
                .patientName(patient.getName())
                .age(patient.getAge())
                .gender(patient.getGender())
                .symptoms(patient.getSymptoms())
                .preKTAS(patient.getPreKtas())
                .build();
    }

}
