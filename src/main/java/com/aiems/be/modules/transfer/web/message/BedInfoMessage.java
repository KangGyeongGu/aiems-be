package com.aiems.be.modules.transfer.web.message;

import com.aiems.be.modules.hospital.service.result.BedInfo;
import lombok.Builder;

@Builder
public record BedInfoMessage(
        String bedType,
        Integer totalBedCount,
        Integer availableBedCount
) {

    public static BedInfoMessage from(BedInfo bedInfo) {
        return BedInfoMessage.builder()
                .bedType(bedInfo.bedType())
                .totalBedCount(bedInfo.totalBedCount())
                .availableBedCount(bedInfo.availableBedCount())
                .build();
    }

}