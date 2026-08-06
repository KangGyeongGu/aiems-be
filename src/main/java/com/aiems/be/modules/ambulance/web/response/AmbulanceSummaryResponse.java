package com.aiems.be.modules.ambulance.web.response;

import lombok.Builder;

@Builder
public record AmbulanceSummaryResponse(
    String licensePlate,
    String fireStationName
) {}