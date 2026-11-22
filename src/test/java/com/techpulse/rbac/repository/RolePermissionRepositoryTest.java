package com.techpulse.rbac.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.techpulse.rbac.entity.Permission;
import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.entity.RolePermission;
import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RolePermissionRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Test
    void checksPermissionThroughDatabaseMappings() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("bcrypt-placeholder");
        user.setActive(true);
        user = userRepository.save(user);

        Role role = new Role();
        role.setName("TEST_ROLE");
        role = roleRepository.save(role);

        Permission permission = new Permission();
        permission.setName("TEST_PERMISSION");
        permission = permissionRepository.save(permission);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission = rolePermissionRepository.save(rolePermission);
        rolePermissionRepository.flush();

        assertTrue(rolePermissionRepository.hasPermission(
                "alice", "TEST_PERMISSION"));

        rolePermissionRepository.delete(rolePermission);
        rolePermissionRepository.flush();

        assertFalse(rolePermissionRepository.hasPermission(
                "alice", "TEST_PERMISSION"));
    }
}