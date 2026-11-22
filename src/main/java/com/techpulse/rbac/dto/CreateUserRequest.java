package com.techpulse.rbac.dto;

import com.techpulse.rbac.constant.AppMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = AppMessages.USERNAME_REQUIRED)
    @Size(max = 255, message = AppMessages.USERNAME_TOO_LONG)
    private String username;

    @NotBlank(message = AppMessages.PASSWORD_REQUIRED)
    @Size(min = 8, max = 255, message = AppMessages.PASSWORD_LENGTH_INVALID)
    private String password;
}
