package com.aiems.be.modules.hospital.bed.service;


public record BedInfo(
        String bedType,
        Integer totalBedCount,
        Integer availableBedCount
) {
}
