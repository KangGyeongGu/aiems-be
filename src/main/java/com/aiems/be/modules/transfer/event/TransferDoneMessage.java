package com.aiems.be.modules.transfer.event;

public record TransferDoneMessage(
        Long ambulanceId,
        Boolean done
) {
    public static TransferDoneMessage done(Long ambulanceId) {
        return new TransferDoneMessage(ambulanceId, true);
    }
}
