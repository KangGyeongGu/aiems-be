package com.aiems.be.contracts.bed;


public record BedInfo(
        String bedType,
        Integer totalBedCount,
        Integer availableBedCount
) {
}
