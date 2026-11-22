package com.techpulse.rbac.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techpulse.rbac.repository.RolePermissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RoleSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolePermissionRepository rolePermissionRepository;

    @Test
    void unauthenticatedUserIsDenied() throws Exception {
        mockMvc.perform(post("/roles")
                        .contentType("application/json")
                        .content("{\"name\":\"AUDITOR\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice")
    void authenticatedUserWithoutPermissionIsDenied() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "alice", "MANAGE_ROLES")).thenReturn(false);

        mockMvc.perform(post("/roles")
                        .contentType("application/json")
                        .content("{\"name\":\"AUDITOR\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin")
    void authenticatedUserWithPermissionCreatesRole() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "admin", "MANAGE_ROLES")).thenReturn(true);

        mockMvc.perform(post("/roles")
                        .contentType("application/json")
                        .content("{\"name\":\"AUDITOR\"}"))
                .andExpect(status().isCreated());
    }
}
