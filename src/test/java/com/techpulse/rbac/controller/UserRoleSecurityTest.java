package com.techpulse.rbac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.entity.UserRole;
import com.techpulse.rbac.repository.RoleRepository;
import com.techpulse.rbac.repository.RolePermissionRepository;
import com.techpulse.rbac.repository.UserRepository;
import com.techpulse.rbac.repository.UserRoleRepository;
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
class UserRoleSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolePermissionRepository rolePermissionRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private UserRoleRepository userRoleRepository;

    @Test
    void unauthenticatedUserIsDenied() throws Exception {
        mockMvc.perform(post("/users/10/roles/20"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice")
    void authenticatedUserWithoutPermissionIsDenied() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "alice", "ASSIGN_ROLES")).thenReturn(false);

        mockMvc.perform(post("/users/10/roles/20"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    void authenticatedUserWithPermissionCanAssignRole() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "admin", "ASSIGN_ROLES")).thenReturn(true);
        User user = new User();
        user.setId(10L);
        Role role = new Role();
        role.setId(20L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(20L)).thenReturn(Optional.of(role));
        when(userRoleRepository.existsByUserIdAndRoleId(10L, 20L))
                .thenReturn(false);
        when(userRoleRepository.save(any(UserRole.class)))
                .thenAnswer(invocation -> {
                    UserRole mapping = invocation.getArgument(0);
                    mapping.setId(30L);
                    return mapping;
                });

        mockMvc.perform(post("/users/10/roles/20"))
                .andExpect(status().isCreated());
    }
}
