package com.techpulse.rbac.service.impl;

import com.techpulse.rbac.constant.AppMessages;
import com.techpulse.rbac.dto.RolePermissionResponse;
import com.techpulse.rbac.entity.Permission;
import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.entity.RolePermission;
import com.techpulse.rbac.exception.DuplicateRolePermissionException;
import com.techpulse.rbac.exception.ResourceNotFoundException;
import com.techpulse.rbac.repository.PermissionRepository;
import com.techpulse.rbac.repository.RolePermissionRepository;
import com.techpulse.rbac.repository.RoleRepository;
import com.techpulse.rbac.service.RolePermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private static final Logger log =
            LoggerFactory.getLogger(RolePermissionServiceImpl.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionServiceImpl(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional
    public RolePermissionResponse assignPermission(
            Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.ROLE_NOT_FOUND));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.PERMISSION_NOT_FOUND));

        if (rolePermissionRepository
                .existsByRoleIdAndPermissionId(roleId, permissionId)) {
            log.warn("Permission assignment denied: duplicate mapping");
            throw new DuplicateRolePermissionException();
        }

        RolePermission mapping = new RolePermission();
        mapping.setRole(role);
        mapping.setPermission(permission);
        RolePermission savedMapping =
                rolePermissionRepository.save(mapping);
        log.info("Permission assigned to role");

        return new RolePermissionResponse(
                savedMapping.getId(),
                savedMapping.getRole().getId(),
                savedMapping.getPermission().getId());
    }
}
