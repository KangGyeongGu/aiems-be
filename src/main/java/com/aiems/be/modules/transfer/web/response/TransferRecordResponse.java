package com.aiems.be.modules.transfer.web.response;

import com.aiems.be.common.web.response.LocationResponse;
import com.aiems.be.modules.ambulance.web.response.AmbulanceResponse;
import com.aiems.be.modules.hospital.web.response.HospitalResponse;
import com.aiems.be.modules.patient.web.response.PatientResponse;
import com.aiems.be.modules.transfer.repository.dto.TransferRecordDto;
import lombok.Builder;

import java.time.Instant;

@Builder
public record TransferRecordResponse(
        Long transferId,
        PatientResponse patient,
        AmbulanceResponse ambulance,
        HospitalResponse hospital,
        LocationResponse accidentLocation,
        Instant startedAt,
        Instant endedAt
) {

    public static TransferRecordResponse from(TransferRecordDto transferRecord) {
        return TransferRecordResponse.builder()
                .transferId(transferRecord.getId())
                .patient(PatientResponse.from(transferRecord.getPatient()))
                .ambulance(AmbulanceResponse.from(transferRecord.getAmbulance()))
                .hospital(HospitalResponse.from(transferRecord.getHospital()))
                .accidentLocation(LocationResponse.from(transferRecord.getLocation()))
                .startedAt(transferRecord.getStartedAt())
                .endedAt(transferRecord.getEndedAt())
                .build();
    }

}
