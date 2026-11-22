package com.techpulse.rbac.controller;

import com.techpulse.rbac.dto.UserRoleResponse;
import com.techpulse.rbac.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRoleController {

    private static final Logger log =
            LoggerFactory.getLogger(UserRoleController.class);

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasPermission(null, 'ASSIGN_ROLES')")
    public ResponseEntity<UserRoleResponse> assignRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        if (userId == null || userId <= 0
                || roleId == null || roleId <= 0) {
            throw new IllegalArgumentException(
                    "User and role IDs must be positive");
        }

        log.debug("User role assignment request received");
        UserRoleResponse response =
                userRoleService.assignRole(userId, roleId);
        log.info("User role assignment response returned");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
