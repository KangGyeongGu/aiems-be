package com.aiems.be.transfer.repository.projection;

import com.aiems.be.common.domain.Gender;
import com.aiems.be.common.domain.PreKTAS;

import java.time.Instant;

public record TransferRecordSummary(
        Long id,
        Instant endedAt,
        String ambulanceLicensePlate,
        String ambulanceFireStationName,
        Long patientId,
        String patientName,
        Integer patientAge,
        Gender patientGender,
        String patientSymptoms,
        PreKTAS preKtas
) {
}
