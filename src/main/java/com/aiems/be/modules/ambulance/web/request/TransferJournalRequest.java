package com.aiems.be.modules.ambulance.web.request;

import jakarta.validation.constraints.NotBlank;

public record TransferJournalRequest(
        @NotBlank
        String message
) {
}
