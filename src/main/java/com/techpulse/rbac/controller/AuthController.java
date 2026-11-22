package com.techpulse.rbac.controller;

import com.techpulse.rbac.dto.LoginRequest;
import com.techpulse.rbac.dto.LoginResponse;
import com.techpulse.rbac.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        log.debug("Login request received");
        LoginResponse response = authService.login(request);
        log.info("Login response returned");
        return ResponseEntity.ok(response);
    }
}
