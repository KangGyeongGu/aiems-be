package com.aiems.be.modules.patient.web.response;

import com.aiems.be.modules.patient.domain.Gender;
import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.patient.domain.PreKTAS;
import lombok.Builder;

@Builder
public record PatientResponse(
        Long id,
        String name,
        Gender gender,
        Integer age,
        VitalSignResponse vitalSign,
        String symptoms,
        PreKTAS preKTAS
) {

    public static PatientResponse from(Patient patient) {

        if (patient == null) { return null; }

        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .age(patient.getAge())
                .gender(patient.getGender())
                .vitalSign(VitalSignResponse.from(patient.getVitalSign()))
                .symptoms(patient.getSymptoms())
                .preKTAS(patient.getPreKtas())
                .build();
    }
}