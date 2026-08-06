package com.aiems.be.modules.transfer.service.command;

import lombok.Builder;

import java.time.Instant;
import java.util.Objects;

@Builder
public record TransferRecordInitCommand(
        Long ambulanceId,
        Long hospitalId,
        Instant startedAt
) {

    public TransferRecordInitCommand {
        startedAt = Objects.requireNonNullElse(startedAt, Instant.now());
    }

}