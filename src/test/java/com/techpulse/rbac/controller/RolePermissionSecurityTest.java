package com.techpulse.rbac.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techpulse.rbac.repository.RolePermissionRepository;
import com.techpulse.rbac.repository.RoleRepository;
import com.techpulse.rbac.repository.PermissionRepository;
import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.entity.Permission;
import com.techpulse.rbac.entity.RolePermission;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RolePermissionSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolePermissionRepository rolePermissionRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private PermissionRepository permissionRepository;

    @Test
    void unauthenticatedUserIsDenied() throws Exception {
        mockMvc.perform(post("/roles/1/permissions/2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice")
    void authenticatedUserWithoutPermissionIsDenied() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "alice", "ASSIGN_PERMISSIONS")).thenReturn(false);

        mockMvc.perform(post("/roles/1/permissions/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    void authenticatedUserWithPermissionCanAssign() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "admin", "ASSIGN_PERMISSIONS")).thenReturn(true);
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

        mockMvc.perform(post("/roles/1/permissions/2"))
                .andExpect(status().isCreated());
    }
}
