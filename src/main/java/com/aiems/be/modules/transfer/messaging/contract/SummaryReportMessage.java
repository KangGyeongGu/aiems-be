package com.aiems.be.modules.transfer.messaging.contract;

public record SummaryReportMessage(
        Long ambulanceId,
        Long patientId,
        String reportKey
) {
}
