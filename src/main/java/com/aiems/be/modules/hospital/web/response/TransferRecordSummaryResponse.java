package com.aiems.be.modules.hospital.web.response;

import com.aiems.be.modules.transfer.domain.Gender;
import com.aiems.be.modules.transfer.domain.PreKTAS;
import com.aiems.be.modules.transfer.repository.projection.TransferRecordSummary;

import java.time.Instant;

public record TransferRecordSummaryResponse(
        Long transferId,
        Instant endedAt,
        AmbulanceSummary ambulance,
        PatientSummary patient
) {
    public static TransferRecordSummaryResponse from(TransferRecordSummary summary) {
        return new TransferRecordSummaryResponse(
                summary.id(),
                summary.endedAt(),
                new AmbulanceSummary(summary.ambulanceLicensePlate(), summary.ambulanceFireStationName()),
                new PatientSummary(summary.patientId(), summary.patientName(), summary.patientAge(),
                        summary.patientGender(), summary.patientSymptoms(), summary.preKtas())
        );
    }

    public record AmbulanceSummary(String licensePlate, String fireStationName) {
    }

    public record PatientSummary(Long patientId, String patientName, Integer age,
                                 Gender gender, String symptoms, PreKTAS preKtas) {
    }
}
