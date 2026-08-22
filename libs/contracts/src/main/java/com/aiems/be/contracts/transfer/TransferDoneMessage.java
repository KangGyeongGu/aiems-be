package com.aiems.be.contracts.transfer;

public record TransferDoneMessage(
        Long ambulanceId,
        Boolean done
) {
    public static TransferDoneMessage done(Long ambulanceId) {
        return new TransferDoneMessage(ambulanceId, true);
    }
}
