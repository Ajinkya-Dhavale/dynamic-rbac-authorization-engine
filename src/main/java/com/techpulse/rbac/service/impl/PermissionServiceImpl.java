package com.techpulse.rbac.service.impl;

import com.techpulse.rbac.dto.CreatePermissionRequest;
import com.techpulse.rbac.dto.PermissionResponse;
import com.techpulse.rbac.entity.Permission;
import com.techpulse.rbac.exception.DuplicatePermissionException;
import com.techpulse.rbac.repository.PermissionRepository;
import com.techpulse.rbac.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger log =
            LoggerFactory.getLogger(PermissionServiceImpl.class);

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(
            PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public PermissionResponse create(CreatePermissionRequest request) {
        String name = request.getName().trim();
        if (permissionRepository.findByNameIgnoreCase(name).isPresent()) {
            log.warn("Permission creation denied: duplicate permission");
            throw new DuplicatePermissionException();
        }

        Permission permission = new Permission();
        permission.setName(name);
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created");
        return new PermissionResponse(
                savedPermission.getId(), savedPermission.getName());
    }
}
