package com.aiems.be.modules.transfer.web.message;

import lombok.Builder;

@Builder
public record PatientTransferCompleteMessage(
        Long ambulanceId,
        Boolean accepted
) {

    public static PatientTransferCompleteMessage AcceptMessage(Long ambulanceId) {
        return PatientTransferCompleteMessage.builder()
                .ambulanceId(ambulanceId)
                .accepted(true)
                .build();
    }

    public static PatientTransferCompleteMessage DenyMessage(Long ambulanceId) {
        return PatientTransferCompleteMessage.builder()
                .ambulanceId(ambulanceId)
                .accepted(false)
                .build();
    }

}