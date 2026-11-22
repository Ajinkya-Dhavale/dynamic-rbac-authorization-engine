package com.techpulse.rbac.constant;

public final class AppMessages {

    private AppMessages() {
    }

    public static final String INVALID_CREDENTIALS =
            "Invalid username or password";
    public static final String VALIDATION_FAILED =
            "Validation failed";
    public static final String MALFORMED_JSON =
            "Malformed JSON request";
    public static final String INVALID_PARAMETER =
            "Invalid request parameter";
    public static final String AUTHENTICATION_REQUIRED =
            "Authentication required";
    public static final String ACCESS_DENIED =
            "Access denied";
    public static final String INTERNAL_SERVER_ERROR =
            "Internal server error";

    public static final String USERNAME_REQUIRED =
            "Username is required";
    public static final String USERNAME_TOO_LONG =
            "Username must not exceed 255 characters";
    public static final String PASSWORD_REQUIRED =
            "Password is required";
    public static final String PASSWORD_LENGTH_INVALID =
            "Password must contain between 8 and 255 characters";
    public static final String ROLE_NAME_REQUIRED =
            "Role name is required";
    public static final String ROLE_NAME_TOO_LONG =
            "Role name must not exceed 100 characters";
    public static final String ROLE_ALREADY_EXISTS =
            "Role already exists";
    public static final String USER_ALREADY_EXISTS =
            "Username already exists";
    public static final String PERMISSION_NAME_REQUIRED =
            "Permission name is required";
    public static final String PERMISSION_NAME_TOO_LONG =
            "Permission name must not exceed 100 characters";
    public static final String PERMISSION_ALREADY_EXISTS =
            "Permission already exists";
}
