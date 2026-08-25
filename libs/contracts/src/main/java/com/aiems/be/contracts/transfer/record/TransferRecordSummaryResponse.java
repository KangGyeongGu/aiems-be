package com.aiems.be.contracts.transfer.record;

import com.aiems.be.common.domain.Gender;
import com.aiems.be.common.domain.PreKTAS;

import java.time.Instant;

public record TransferRecordSummaryResponse(
        Long transferId,
        Instant endedAt,
        AmbulanceSummary ambulance,
        PatientSummary patient
) {
    public record AmbulanceSummary(String licensePlate, String fireStationName) {
    }

    public record PatientSummary(Long patientId, String patientName, Integer age,
                                 Gender gender, String symptoms, PreKTAS preKtas) {
    }
}
