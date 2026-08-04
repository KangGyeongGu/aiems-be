package com.aiems.be.modules.transfer.web.dto;

import lombok.Builder;

@Builder
public record AmbulanceDto(
        Long id,
        String deviceId,
        String licensePlate,
        String fireStationName,
        String jurisdiction,
        String operationStatus
) {
}
