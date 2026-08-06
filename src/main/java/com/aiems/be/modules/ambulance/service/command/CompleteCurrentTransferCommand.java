package com.aiems.be.modules.ambulance.service.command;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CompleteCurrentTransferCommand(
        Long ambulanceId,
        Instant completedAt
) {
}
