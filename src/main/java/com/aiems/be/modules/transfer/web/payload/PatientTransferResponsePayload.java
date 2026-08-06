package com.aiems.be.modules.transfer.web.payload;

import lombok.Builder;

@Builder
public record PatientTransferResponsePayload(
        Long ambulanceId,
        Boolean accepted
) {
}
