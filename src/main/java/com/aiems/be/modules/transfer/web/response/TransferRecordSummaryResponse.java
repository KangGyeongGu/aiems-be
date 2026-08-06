package com.aiems.be.modules.transfer.web.response;

import com.aiems.be.modules.ambulance.web.response.AmbulanceSummaryResponse;
import com.aiems.be.modules.patient.web.response.PatientSummaryResponse;
import com.aiems.be.modules.transfer.repository.dto.TransferRecordSummaryDto;
import lombok.Builder;

import java.time.Instant;

@Builder
public record TransferRecordSummaryResponse (
        Long transferId,
        Instant endedAt,
        AmbulanceSummaryResponse ambulance,
        PatientSummaryResponse patient
) {
    public static TransferRecordSummaryResponse from(TransferRecordSummaryDto dto) {
        return TransferRecordSummaryResponse.builder()
                .transferId(dto.getId())
                .endedAt(dto.getEndedAt())
                .ambulance(AmbulanceSummaryResponse.builder().
                        licensePlate(dto.getAmbulanceLicensePlate())
                        .fireStationName(dto.getAmbulanceFireStationName())
                        .build()
                )
                .patient(PatientSummaryResponse.builder()
                        .patientId(dto.getPatientId())
                        .patientName(dto.getPatientName())
                        .age(dto.getPatientAge())
                        .gender(dto.getPatientGender())
                        .symptoms(dto.getPatientSymptoms())
                        .preKTAS(dto.getPreKTAS())
                        .build()
                )
                .build();
    }
}

