package com.techpulse.rbac.exception;

import com.techpulse.rbac.constant.AppMessages;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super(AppMessages.INVALID_CREDENTIALS);
    }
}
