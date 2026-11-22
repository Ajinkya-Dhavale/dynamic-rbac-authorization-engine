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
public class CreatePermissionRequest {

    @NotBlank(message = AppMessages.PERMISSION_NAME_REQUIRED)
    @Size(max = 100, message = AppMessages.PERMISSION_NAME_TOO_LONG)
    private String name;
}
