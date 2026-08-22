package com.aiems.be.hospital.web.request;

public record TransferResponseRequest(
        Long ambulanceId,
        Boolean accepted
) {
}
