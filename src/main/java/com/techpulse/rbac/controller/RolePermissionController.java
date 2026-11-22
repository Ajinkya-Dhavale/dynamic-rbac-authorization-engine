package com.techpulse.rbac.controller;

import com.techpulse.rbac.dto.RolePermissionResponse;
import com.techpulse.rbac.service.RolePermissionService;
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
@RequestMapping("/roles")
public class RolePermissionController {

    private static final Logger log =
            LoggerFactory.getLogger(RolePermissionController.class);

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(
            RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasPermission(null, 'ASSIGN_PERMISSIONS')")
    public ResponseEntity<RolePermissionResponse> assignPermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        if (roleId == null || roleId <= 0
                || permissionId == null || permissionId <= 0) {
            throw new IllegalArgumentException(
                    "Role and permission IDs must be positive");
        }

        log.debug("Role permission assignment request received");
        RolePermissionResponse response =
                rolePermissionService.assignPermission(
                        roleId, permissionId);
        log.info("Role permission assignment response returned");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
