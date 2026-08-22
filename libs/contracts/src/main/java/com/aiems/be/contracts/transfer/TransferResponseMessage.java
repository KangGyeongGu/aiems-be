package com.aiems.be.contracts.transfer;

public record TransferResponseMessage(
        Long hospitalId,
        Boolean accepted
) {
    public static TransferResponseMessage of(Long hospitalId, Boolean accepted) {
        return new TransferResponseMessage(hospitalId, accepted);
    }
}
