package com.aiems.be.modules.hospital.socket.payload;

public record TransferResponsePayload(
        Long ambulanceId,
        Boolean accepted
) {
}
