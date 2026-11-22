package com.techpulse.rbac.service;

import com.techpulse.rbac.dto.UserRoleResponse;

public interface UserRoleService {
    UserRoleResponse assignRole(Long userId, Long roleId);
}
