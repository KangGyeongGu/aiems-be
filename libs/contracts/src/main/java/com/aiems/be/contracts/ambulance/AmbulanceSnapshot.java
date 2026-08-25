package com.aiems.be.contracts.ambulance;

public record AmbulanceSnapshot(
        Long id,
        String deviceId,
        String licensePlate,
        String fireStationName,
        String jurisdiction,
        String operationStatus
) {
}
