package com.aiems.be.contracts.transfer.record;

import com.aiems.be.common.domain.Gender;
import com.aiems.be.common.domain.PreKTAS;

import java.time.Instant;

public record TransferRecordSearchRequest(
        String patientName,
        Integer patientAge,
        Gender patientGender,
        String symptoms,
        PreKTAS preKtas,
        String ambulanceLicensePlate,
        String fireStationName,
        Instant startedAtFrom,
        Instant startedAtTo,
        Instant endedAtFrom,
        Instant endedAtTo
) {
}
