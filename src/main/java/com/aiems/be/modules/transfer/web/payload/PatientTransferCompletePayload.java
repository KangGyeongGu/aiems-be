package com.aiems.be.modules.transfer.web.payload;

import lombok.Builder;

@Builder
public record PatientTransferCompletePayload(
        Long hospitalId,
        Boolean accepted
) {
    public boolean isDeniedHospital(Long hospitalId) {
        return !hospitalId().equals(hospitalId);
    }
}
