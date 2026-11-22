package com.techpulse.rbac.exception;

import com.techpulse.rbac.constant.AppMessages;

public class DuplicateRoleException extends RuntimeException {
    public DuplicateRoleException() {
        super(AppMessages.ROLE_ALREADY_EXISTS);
    }
}
