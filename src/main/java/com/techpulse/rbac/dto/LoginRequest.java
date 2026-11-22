package com.techpulse.rbac.dto;

import com.techpulse.rbac.constant.AppMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = AppMessages.USERNAME_REQUIRED)
        @Size(max = 255, message = AppMessages.USERNAME_TOO_LONG)
        String username,

        @NotBlank(message = AppMessages.PASSWORD_REQUIRED)
        @Size(min = 8, max = 255, message = AppMessages.PASSWORD_LENGTH_INVALID)
        String password
) {
}
