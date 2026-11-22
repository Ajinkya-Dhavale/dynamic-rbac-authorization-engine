package com.techpulse.rbac.exception;

import com.techpulse.rbac.constant.AppMessages;
import com.techpulse.rbac.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateRoleException.class)
    public ResponseEntity<ApiError> handleDuplicateRole(
            DuplicateRoleException exception,
            HttpServletRequest request) {
        log.warn("Conflict: role already exists");
        return response(HttpStatus.CONFLICT, exception.getMessage(),
                request, Map.of());
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiError> handleDuplicateUser(
            DuplicateUserException exception,
            HttpServletRequest request) {
        log.warn("Conflict: username already exists");
        return response(HttpStatus.CONFLICT, exception.getMessage(),
                request, Map.of());
    }

    @ExceptionHandler(DuplicatePermissionException.class)
    public ResponseEntity<ApiError> handleDuplicatePermission(
            DuplicatePermissionException exception,
            HttpServletRequest request) {
        log.warn("Conflict: permission already exists");
        return response(HttpStatus.CONFLICT, exception.getMessage(),
                request, Map.of());
    }

    @ExceptionHandler(DuplicateRolePermissionException.class)
    public ResponseEntity<ApiError> handleDuplicateRolePermission(
            DuplicateRolePermissionException exception,
            HttpServletRequest request) {
        log.warn("Conflict: role permission mapping already exists");
        return response(HttpStatus.CONFLICT, exception.getMessage(),
                request, Map.of());
    }

    @ExceptionHandler(DuplicateUserRoleException.class)
    public ResponseEntity<ApiError> handleDuplicateUserRole(
            DuplicateUserRoleException exception,
            HttpServletRequest request) {
        log.warn("Conflict: user role mapping already exists");
        return response(HttpStatus.CONFLICT, exception.getMessage(),
                request, Map.of());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {
        log.warn("Not found: {}", exception.getMessage());
        return response(HttpStatus.NOT_FOUND, exception.getMessage(),
                request, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        log.warn("Conflict: data constraint violated");
        return response(HttpStatus.CONFLICT,
                AppMessages.ROLE_ALREADY_EXISTS, request, Map.of());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request) {
        log.warn("Unauthorized: {}", request.getRequestURI());
        return response(HttpStatus.UNAUTHORIZED, exception.getMessage(),
                request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Bad request: validation failed for {}", request.getRequestURI());
        return response(HttpStatus.BAD_REQUEST, AppMessages.VALIDATION_FAILED,
                request, fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        log.warn("Bad request: malformed JSON");
        return response(HttpStatus.BAD_REQUEST, AppMessages.MALFORMED_JSON,
                request, Map.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        log.warn("Bad request: invalid parameter");
        return response(HttpStatus.BAD_REQUEST, AppMessages.INVALID_PARAMETER,
                request, Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        log.warn("Bad request: {}", exception.getMessage());
        return response(HttpStatus.BAD_REQUEST, AppMessages.INVALID_PARAMETER,
                request, Map.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException exception,
            HttpServletRequest request) {
        log.warn("Unauthorized: authentication required");
        return response(HttpStatus.UNAUTHORIZED,
                AppMessages.AUTHENTICATION_REQUIRED,
                request, Map.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {
        log.warn("Forbidden: access denied");
        return response(HttpStatus.FORBIDDEN, AppMessages.ACCESS_DENIED,
                request, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {
        log.error("Unexpected error: {}", exception.getMessage(), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                AppMessages.INTERNAL_SERVER_ERROR, request, Map.of());
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(status).body(error);
    }
}
