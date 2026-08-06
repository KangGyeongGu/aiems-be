package com.aiems.be.modules.transfer.service.result;

import lombok.Builder;

@Builder
public record CreateTransferDoneMessageResult(
        Long ambulanceId,
        Long visitedHospitalId,
        Boolean isDone
) {
}