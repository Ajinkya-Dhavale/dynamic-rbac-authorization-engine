package com.techpulse.rbac.service;

import com.techpulse.rbac.dto.CreateRoleRequest;
import com.techpulse.rbac.dto.RoleResponse;

public interface RoleService {
    RoleResponse create(CreateRoleRequest request);
}
