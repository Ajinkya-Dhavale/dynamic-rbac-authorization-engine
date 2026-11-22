package com.techpulse.rbac.service;

import com.techpulse.rbac.dto.CreatePermissionRequest;
import com.techpulse.rbac.dto.PermissionResponse;

public interface PermissionService {
    PermissionResponse create(CreatePermissionRequest request);
}
