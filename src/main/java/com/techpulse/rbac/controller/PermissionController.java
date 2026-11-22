package com.techpulse.rbac.controller;

import com.techpulse.rbac.dto.CreatePermissionRequest;
import com.techpulse.rbac.dto.PermissionResponse;
import com.techpulse.rbac.service.PermissionService;
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
@RequestMapping("/permissions")
public class PermissionController {

    private static final Logger log =
            LoggerFactory.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'MANAGE_PERMISSIONS')")
    public ResponseEntity<PermissionResponse> create(
            @Valid @RequestBody CreatePermissionRequest request) {
        log.debug("Permission creation request received");
        PermissionResponse response = permissionService.create(request);
        log.info("Permission creation response returned");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
