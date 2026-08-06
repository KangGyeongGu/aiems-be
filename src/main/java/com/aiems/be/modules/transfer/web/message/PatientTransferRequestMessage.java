package com.aiems.be.modules.transfer.web.message;

import com.aiems.be.modules.ambulance.domain.Ambulance;
import com.aiems.be.modules.ambulance.web.response.AmbulanceResponse;
import com.aiems.be.common.web.response.LocationResponse;
import com.aiems.be.modules.patient.domain.Patient;
import com.aiems.be.modules.patient.web.response.PatientResponse;
import com.aiems.be.modules.transfer.web.payload.PatientTransferRequestPayload;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PatientTransferRequestMessage (
        AmbulanceResponse ambulance,
        PatientResponse patient,
        String firstAid,                    // 응급 처치 내용
        List<String> underlyingDisease,     // 기저 질환
        String cause,                       // 사고 원인
        LocationResponse accidentLocation,
        Double distance,
        Instant startedAt
) {

    public static PatientTransferRequestMessage of(Patient patient, Ambulance ambulance, PatientTransferRequestPayload payload, Double distance) {
        return PatientTransferRequestMessage.builder()
                .patient(PatientResponse.from(patient))
                .ambulance(AmbulanceResponse.from(ambulance))
                .accidentLocation(payload.accidentLocation())
                .cause(payload.cause())
                .underlyingDisease(payload.underlyingDisease())
                .firstAid(payload.firstAid())
                .startedAt(Instant.now())
                .distance(distance)
                .build();
    }

}