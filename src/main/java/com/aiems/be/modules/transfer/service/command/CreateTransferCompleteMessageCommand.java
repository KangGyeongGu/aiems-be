package com.aiems.be.modules.transfer.service.command;

import lombok.Builder;

@Builder
public record CreateTransferCompleteMessageCommand(
        Long ambulanceId,
        Long acceptedHospitalId
) {

    public boolean isDeniedHospital(Long hospitalId) {
        return !acceptedHospitalId.equals(hospitalId);
    }

}