package com.aiems.be.modules.ambulance.web.response;

import com.aiems.be.modules.ambulance.domain.Ambulance;
import com.aiems.be.modules.ambulance.domain.OperationStatus;
import lombok.Builder;

@Builder
public record AmbulanceResponse(
        Long id,
        String licensePlate,
        String fireStationName,
        OperationStatus operationStatus
) {
    public static AmbulanceResponse from(Ambulance ambulance) {
        return AmbulanceResponse.builder()
                .id(ambulance.getId())
                .licensePlate(ambulance.getLicensePlate())
                .fireStationName(ambulance.getFireStationName())
                .operationStatus(ambulance.getOperationStatus())
                .build();
    }
}