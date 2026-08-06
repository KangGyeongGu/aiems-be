package com.aiems.be.modules.transfer.web.message;

public record PatientTransferDoneMessage(
        Long ambulanceId,
        Boolean done
) {

    public static PatientTransferDoneMessage done(Long ambulanceId) {
        return new PatientTransferDoneMessage(ambulanceId, true);
    }

}