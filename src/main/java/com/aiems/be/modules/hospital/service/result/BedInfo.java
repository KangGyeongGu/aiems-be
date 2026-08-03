package com.aiems.be.modules.hospital.service.result;

import lombok.Builder;

@Builder
public record BedInfo(
        String bedType,
        Integer totalBedCount,
        Integer availableBedCount
) {
}
