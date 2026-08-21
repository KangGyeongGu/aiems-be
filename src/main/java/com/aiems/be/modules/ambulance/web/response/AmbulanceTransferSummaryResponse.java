package com.aiems.be.modules.ambulance.web.response;

import com.aiems.be.modules.transfer.domain.PreKTAS;
import com.aiems.be.modules.transfer.repository.projection.AmbulanceTransferSummary;

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
