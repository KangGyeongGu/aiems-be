package com.aiems.be.contracts.ai;

public record SummaryJobMessage(
        Long ambulanceId,
        String audioKey,
        PatientSnapshot patient
) {
    public record PatientSnapshot(
            Long id,
            AmbulanceSnapshot ambulance,
            String name,
            Integer age,
            String gender,
            VitalSignSnapshot vitalSign,
            String symptoms,
            String preKtas,
            String firstAid,
            String cause,
            String underlyingDisease
    ) {
    }

    public record AmbulanceSnapshot(
            Long id, String deviceId, String licensePlate,
            String fireStationName, String jurisdiction, String operationStatus
    ) {
    }

    public record VitalSignSnapshot(
            Integer minBloodPressure, Integer maxBloodPressure,
            Integer pulse, Integer respiratoryRate, Double temperature
    ) {
    }
}
