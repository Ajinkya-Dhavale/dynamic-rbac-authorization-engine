package com.techpulse.rbac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techpulse.rbac.dto.RolePermissionResponse;
import com.techpulse.rbac.entity.Permission;
import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.entity.RolePermission;
import com.techpulse.rbac.exception.DuplicateRolePermissionException;
import com.techpulse.rbac.exception.ResourceNotFoundException;
import com.techpulse.rbac.repository.PermissionRepository;
import com.techpulse.rbac.repository.RolePermissionRepository;
import com.techpulse.rbac.repository.RoleRepository;
import com.techpulse.rbac.service.impl.RolePermissionServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RolePermissionServiceImplTest {

    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private RolePermissionRepository rolePermissionRepository;
    private RolePermissionServiceImpl service;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        permissionRepository = mock(PermissionRepository.class);
        rolePermissionRepository = mock(RolePermissionRepository.class);
        service = new RolePermissionServiceImpl(
                roleRepository,
                permissionRepository,
                rolePermissionRepository);
    }

    @Test
    void assignsPermissionSuccessfully() {
        Role role = new Role();
        role.setId(1L);
        role.setName("AUDITOR");
        Permission permission = new Permission();
        permission.setId(2L);
        permission.setName("EXPORT_REPORT");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(2L))
                .thenReturn(Optional.of(permission));
        when(rolePermissionRepository
                .existsByRoleIdAndPermissionId(1L, 2L))
                .thenReturn(false);
        when(rolePermissionRepository.save(
                org.mockito.ArgumentMatchers.any(RolePermission.class)))
                .thenAnswer(invocation -> {
                    RolePermission mapping = invocation.getArgument(0);
                    mapping.setId(3L);
                    return mapping;
                });

        RolePermissionResponse response =
                service.assignPermission(1L, 2L);

        assertEquals(3L, response.getId());
        assertEquals(1L, response.getRoleId());
        assertEquals(2L, response.getPermissionId());
    }

    @Test
    void rejectsDuplicateMapping() {
        Role role = new Role();
        role.setId(1L);
        Permission permission = new Permission();
        permission.setId(2L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(2L))
                .thenReturn(Optional.of(permission));
        when(rolePermissionRepository
                .existsByRoleIdAndPermissionId(1L, 2L))
                .thenReturn(true);

        assertThrows(DuplicateRolePermissionException.class,
                () -> service.assignPermission(1L, 2L));
    }

    @Test
    void rejectsMissingRole() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.assignPermission(99L, 2L));
    }

    @Test
    void rejectsMissingPermission() {
        Role role = new Role();
        role.setId(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.assignPermission(1L, 99L));
    }
}
