package com.aiems.be.transfer.web.response;

import com.aiems.be.common.domain.PreKTAS;
import com.aiems.be.transfer.repository.projection.AmbulanceTransferSummary;

import java.time.Instant;

public record AmbulanceTransferSummaryResponse(
        Long transferRecordId,
        Instant startedAt,
        Instant endedAt,
        String hospitalName,
        String patientName,
        PreKTAS preKtas,
        boolean reportReady
) {
    public static AmbulanceTransferSummaryResponse from(AmbulanceTransferSummary summary) {
        return new AmbulanceTransferSummaryResponse(
                summary.id(),
                summary.startedAt(),
                summary.endedAt(),
                summary.hospitalName(),
                summary.patientName(),
                summary.preKtas(),
                summary.transferReport() != null);
    }
}
