package com.techpulse.rbac.exception;

import com.techpulse.rbac.constant.AppMessages;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException() {
        super(AppMessages.USER_ALREADY_EXISTS);
    }
}
