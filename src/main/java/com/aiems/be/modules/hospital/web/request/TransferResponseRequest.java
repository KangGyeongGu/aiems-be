package com.aiems.be.modules.hospital.web.request;

public record TransferResponseRequest(
        Long ambulanceId,
        Boolean accepted
) {
}
