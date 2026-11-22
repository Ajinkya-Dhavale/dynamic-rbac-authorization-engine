package com.techpulse.rbac.dto;

import com.techpulse.rbac.constant.AppMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateRoleRequest {

    @NotBlank(message = AppMessages.ROLE_NAME_REQUIRED)
    @Size(max = 100, message = AppMessages.ROLE_NAME_TOO_LONG)
    private String name;

    public CreateRoleRequest() {
    }

    public CreateRoleRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
