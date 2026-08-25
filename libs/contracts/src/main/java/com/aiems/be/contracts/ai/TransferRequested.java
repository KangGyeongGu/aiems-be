package com.aiems.be.contracts.ai;

public record TransferRequested(
        Long patientId,
        Long ambulanceId,
        PatientAnalysisRequest analysisRequest
) {
}
