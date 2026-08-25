package com.aiems.be.auth.web.request;

import jakarta.validation.constraints.NotBlank;

public record AmbulanceLoginRequest(
        @NotBlank
        String loginId,

        @NotBlank
        String password,

        @NotBlank
        String deviceId
) {
}
