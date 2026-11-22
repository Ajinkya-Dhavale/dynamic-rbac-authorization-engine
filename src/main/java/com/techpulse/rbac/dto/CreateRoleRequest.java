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
public class CreateRoleRequest {

    @NotBlank(message = AppMessages.ROLE_NAME_REQUIRED)
    @Size(max = 100, message = AppMessages.ROLE_NAME_TOO_LONG)
    private String name;

}
