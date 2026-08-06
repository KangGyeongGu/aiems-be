package com.aiems.be.modules.transfer.service.command;

import lombok.Builder;

import java.util.List;

@Builder
public record TransferRequestSnapshotCommand(
        Long ambulanceId,
        List<Long> hospitalIds
) {
}