package com.techpulse.rbac.service.impl;

import com.techpulse.rbac.dto.CreateRoleRequest;
import com.techpulse.rbac.dto.RoleResponse;
import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.exception.DuplicateRoleException;
import com.techpulse.rbac.repository.RoleRepository;
import com.techpulse.rbac.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log =
            LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        String name = request.getName().trim();
        if (roleRepository.findByNameIgnoreCase(name).isPresent()) {
            log.warn("Role creation denied: duplicate role");
            throw new DuplicateRoleException();
        }

        Role role = new Role();
        role.setName(name);
        Role savedRole = roleRepository.save(role);
        log.info("Role created");
        return new RoleResponse(savedRole.getId(), savedRole.getName());
    }
}
