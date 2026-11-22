package com.techpulse.rbac.service;

import com.techpulse.rbac.dto.RolePermissionResponse;

public interface RolePermissionService {
    RolePermissionResponse assignPermission(
            Long roleId, Long permissionId);
}
