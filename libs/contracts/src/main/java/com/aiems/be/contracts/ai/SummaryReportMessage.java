package com.aiems.be.contracts.ai;

public record SummaryReportMessage(
        Long ambulanceId,
        Long patientId,
        String reportKey
) {
}
