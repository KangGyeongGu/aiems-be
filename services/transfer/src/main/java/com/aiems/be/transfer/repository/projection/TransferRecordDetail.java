package com.aiems.be.transfer.repository.projection;

import com.aiems.be.common.domain.Location;
import com.aiems.be.transfer.domain.Patient;

import java.time.Instant;

public record TransferRecordDetail(
        Long id,
        Long ambulanceId,
        String ambulanceLicensePlate,
        String ambulanceFireStationName,
        Long hospitalId,
        String hospitalName,
        String hospitalAddress,
        Patient patient,
        String transferReport,
        String treatmentRecord,
        Location accidentLocation,
        Instant startedAt,
        Instant endedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
