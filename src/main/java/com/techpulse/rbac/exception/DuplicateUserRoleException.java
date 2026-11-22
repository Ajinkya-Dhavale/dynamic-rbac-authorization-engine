package com.techpulse.rbac.exception;

import com.techpulse.rbac.constant.AppMessages;

public class DuplicateUserRoleException extends RuntimeException {
    public DuplicateUserRoleException() {
        super(AppMessages.USER_ROLE_ALREADY_EXISTS);
    }
}
