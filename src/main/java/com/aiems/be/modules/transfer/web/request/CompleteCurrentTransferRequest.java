package com.aiems.be.modules.transfer.web.request;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CompleteCurrentTransferRequest(
    Instant completedAt
){

}
