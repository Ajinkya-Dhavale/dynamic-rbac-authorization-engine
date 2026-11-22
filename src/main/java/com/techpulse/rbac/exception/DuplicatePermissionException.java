package com.techpulse.rbac.exception;

import com.techpulse.rbac.constant.AppMessages;

public class DuplicatePermissionException extends RuntimeException {
    public DuplicatePermissionException() {
        super(AppMessages.PERMISSION_ALREADY_EXISTS);
    }
}
