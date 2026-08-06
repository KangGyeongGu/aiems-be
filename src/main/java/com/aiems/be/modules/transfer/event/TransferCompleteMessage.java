package com.aiems.be.modules.transfer.event;

public record TransferCompleteMessage(
        Long ambulanceId,
        Boolean accepted
) {
    public static TransferCompleteMessage accept(Long ambulanceId) {
        return new TransferCompleteMessage(ambulanceId, true);
    }

    public static TransferCompleteMessage deny(Long ambulanceId) {
        return new TransferCompleteMessage(ambulanceId, false);
    }
}
