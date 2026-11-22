package com.techpulse.rbac.exception;

import com.techpulse.rbac.constant.AppMessages;

public class DuplicateRolePermissionException extends RuntimeException {
    public DuplicateRolePermissionException() {
        super(AppMessages.ROLE_PERMISSION_ALREADY_EXISTS);
    }
}
