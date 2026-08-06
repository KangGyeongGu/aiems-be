package com.aiems.be.modules.hospital.web.request;

import com.aiems.be.modules.transfer.domain.Gender;
import com.aiems.be.modules.transfer.domain.PreKTAS;
import com.aiems.be.modules.transfer.service.TransferRecordService.TransferRecordSearch;

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
    public TransferRecordSearch toSearch() {
        return new TransferRecordSearch(
                patientName, patientAge, patientGender, symptoms, preKtas,
                ambulanceLicensePlate, fireStationName,
                startedAtFrom, startedAtTo, endedAtFrom, endedAtTo);
    }
}
