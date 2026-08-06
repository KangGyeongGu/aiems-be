package com.aiems.be.modules.ambulance.web.request;

import java.time.Instant;

public record TransferCompletionRequest(
        Instant completedAt
) {
}
