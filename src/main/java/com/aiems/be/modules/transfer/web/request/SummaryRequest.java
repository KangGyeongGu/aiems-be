package com.aiems.be.modules.transfer.web.request;

import jakarta.validation.constraints.NotBlank;

public record SummaryRequest(
        @NotBlank
        String message
) {
}
