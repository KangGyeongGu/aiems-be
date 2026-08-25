package com.aiems.be.transfer.web.request;

public record TransferConfirmRequest(
        Long hospitalId,
        String hospitalName,
        String hospitalAddress
) {
}
