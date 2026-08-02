package com.A.B.modules.auth.web.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String loginId,

        @NotBlank
        String password
) {
}
