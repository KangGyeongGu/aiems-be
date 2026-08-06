package com.aiems.be.modules.transfer.repository.projection;

import com.aiems.be.common.domain.Location;
import com.aiems.be.modules.auth.domain.Ambulance;
import com.aiems.be.modules.auth.domain.Hospital;
import com.aiems.be.modules.transfer.domain.Patient;

import java.time.Instant;

public record TransferRecordDetail(
        Long id,
        Ambulance ambulance,
        Hospital hospital,
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
