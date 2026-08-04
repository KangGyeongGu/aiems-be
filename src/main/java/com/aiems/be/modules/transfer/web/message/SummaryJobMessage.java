package com.aiems.be.modules.transfer.web.message;

import com.aiems.be.modules.transfer.web.dto.PatientDto;
import lombok.Builder;

@Builder
public record SummaryJobMessage(
        Long ambulanceId,
        String message,
        PatientDto patient
) {
}
