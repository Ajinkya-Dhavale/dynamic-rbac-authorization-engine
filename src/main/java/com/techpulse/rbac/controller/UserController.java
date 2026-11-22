package com.techpulse.rbac.controller;

import com.techpulse.rbac.dto.CreateUserRequest;
import com.techpulse.rbac.dto.UserResponse;
import com.techpulse.rbac.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'MANAGE_USERS')")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request) {
        log.debug("User creation request received");
        UserResponse response = userService.create(request);
        log.info("User creation response returned");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
