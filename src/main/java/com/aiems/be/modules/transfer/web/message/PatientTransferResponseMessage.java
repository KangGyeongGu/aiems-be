package com.aiems.be.modules.transfer.web.message;

import lombok.Builder;

@Builder
public record PatientTransferResponseMessage(
        Long hospitalId,
        Boolean accepted
) {
}
