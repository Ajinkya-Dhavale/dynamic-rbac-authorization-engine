package com.techpulse.rbac.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techpulse.rbac.repository.RolePermissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolePermissionRepository rolePermissionRepository;

    @Test
    void unauthenticatedUserIsDenied() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"username\":\"alice\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice")
    void authenticatedUserWithoutPermissionIsDenied() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "alice", "MANAGE_USERS")).thenReturn(false);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"username\":\"bob\",\"password\":\"password123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    void adminWithPermissionCreatesUser() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "admin", "MANAGE_USERS")).thenReturn(true);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"username\":\"bob\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());
    }
}
