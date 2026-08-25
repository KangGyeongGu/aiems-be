package com.aiems.be.contracts.transfer.record;

import java.time.Instant;

public record TransferRecordResponse(
        Long transferId,
        PatientDetail patient,
        AmbulanceDetail ambulance,
        HospitalDetail hospital,
        LocationDetail accidentLocation,
        Instant startedAt,
        Instant endedAt
) {
    public record PatientDetail(Long id, String name, Integer age, String gender,
                                String symptoms, String preKtas) {
    }

    public record AmbulanceDetail(Long id, String licensePlate, String fireStationName) {
    }

    public record HospitalDetail(Long id, String name, String address) {
    }

    public record LocationDetail(Double lat, Double lon, String address) {
    }
}
