package com.aiems.be.contracts.transfer;

import java.time.Instant;

public record TransferRequestMessage(
        AmbulanceInfo ambulance,
        PatientInfo patient,
        LocationInfo accidentLocation,
        Double distance,
        Instant requestedAt
) {
    public record AmbulanceInfo(Long id, String licensePlate, String fireStationName) {
    }

    public record PatientInfo(
            Long id, String name, Integer age, String gender,
            VitalSignInfo vitalSign, String symptoms, String preKtas,
            String firstAid, String cause, String underlyingDisease
    ) {
    }

    public record VitalSignInfo(Integer minBloodPressure, Integer maxBloodPressure,
                                Integer pulse, Integer respiratoryRate, Double temperature) {
    }

    public record LocationInfo(Double lat, Double lon, String address) {
    }
}
