package com.aiems.be.modules.auth.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank
        @Pattern(regexp = "^[a-z0-9]{4,20}$")
        String loginId,

        @NotBlank
        @Size(min = 8, max = 64)
        String password,

        @Email
        String email,

        @Size(max = 30)
        String username
) {
}
