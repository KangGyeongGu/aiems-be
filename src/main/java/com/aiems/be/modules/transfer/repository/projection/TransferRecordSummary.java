package com.aiems.be.modules.transfer.repository.projection;

import com.aiems.be.modules.transfer.domain.Gender;
import com.aiems.be.modules.transfer.domain.PreKTAS;

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
