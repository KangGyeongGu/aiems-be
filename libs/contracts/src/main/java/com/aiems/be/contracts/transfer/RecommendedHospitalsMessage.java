package com.aiems.be.contracts.transfer;

import java.util.List;

public record RecommendedHospitalsMessage(
        List<Item> hospitals
) {
    public static RecommendedHospitalsMessage of(List<Item> hospitals) {
        return new RecommendedHospitalsMessage(hospitals);
    }

    public record Item(
            Long hospitalId,
            String name,
            String address,
            Integer level,
            BedSummary bedInfo,
            Double score,
            Double distance
    ) {
    }

    public record BedSummary(String bedType, Integer totalBedCount, Integer availableBedCount) {
    }
}
