package com.techpulse.rbac.service.impl;

import com.techpulse.rbac.constant.AppMessages;
import com.techpulse.rbac.dto.UserRoleResponse;
import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.entity.UserRole;
import com.techpulse.rbac.exception.DuplicateUserRoleException;
import com.techpulse.rbac.exception.ResourceNotFoundException;
import com.techpulse.rbac.repository.RoleRepository;
import com.techpulse.rbac.repository.UserRepository;
import com.techpulse.rbac.repository.UserRoleRepository;
import com.techpulse.rbac.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private static final Logger log =
            LoggerFactory.getLogger(UserRoleServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public UserRoleResponse assignRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.USER_NOT_FOUND));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AppMessages.ROLE_NOT_FOUND));

        if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            log.warn("Role assignment denied: duplicate mapping");
            throw new DuplicateUserRoleException();
        }

        UserRole mapping = new UserRole();
        mapping.setUser(user);
        mapping.setRole(role);
        UserRole savedMapping = userRoleRepository.save(mapping);
        log.info("Role assigned to user");

        return new UserRoleResponse(
                savedMapping.getId(),
                savedMapping.getUser().getId(),
                savedMapping.getRole().getId());
    }
}
