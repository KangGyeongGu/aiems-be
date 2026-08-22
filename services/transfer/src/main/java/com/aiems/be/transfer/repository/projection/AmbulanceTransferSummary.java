package com.aiems.be.transfer.repository.projection;

import com.aiems.be.common.domain.PreKTAS;

import java.time.Instant;

public record AmbulanceTransferSummary(
        Long id,
        Instant startedAt,
        Instant endedAt,
        String hospitalName,
        String patientName,
        PreKTAS preKtas,
        String transferReport
) {
}
