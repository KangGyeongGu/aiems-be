package com.aiems.be.modules.transfer.event;

public record TransferResponseMessage(
        Long hospitalId,
        Boolean accepted
) {
    public static TransferResponseMessage of(Long hospitalId, Boolean accepted) {
        return new TransferResponseMessage(hospitalId, accepted);
    }
}
