package com.techpulse.rbac.controller;

import com.techpulse.rbac.dto.CreateRoleRequest;
import com.techpulse.rbac.dto.RoleResponse;
import com.techpulse.rbac.service.RoleService;
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
@RequestMapping("/roles")
public class RoleController {

    private static final Logger log =
            LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'MANAGE_ROLES')")
    public ResponseEntity<RoleResponse> create(
            @Valid @RequestBody CreateRoleRequest request) {
        log.debug("Role creation request received");
        RoleResponse response = roleService.create(request);
        log.info("Role creation response returned");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
