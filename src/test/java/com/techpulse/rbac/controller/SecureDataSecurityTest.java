package com.techpulse.rbac.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techpulse.rbac.constant.AppMessages;
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
class SecureDataSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolePermissionRepository rolePermissionRepository;

    @Test
    void unauthenticatedUserIsDenied() throws Exception {
        mockMvc.perform(get("/secure-data"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice")
    void authenticatedUserWithoutPermissionIsDenied() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "alice", "READ_SECURE_DATA")).thenReturn(false);

        mockMvc.perform(get("/secure-data"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user")
    void authenticatedUserWithPermissionIsGranted() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "user", "READ_SECURE_DATA")).thenReturn(true);

        mockMvc.perform(get("/secure-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(AppMessages.SECURE_DATA_ACCESS_GRANTED));
    }

    @Test
    @WithMockUser(username = "user")
    void permissionRemovalImmediatelyDeniesAccess() throws Exception {
        when(rolePermissionRepository.hasPermission(
                "user", "READ_SECURE_DATA"))
                .thenReturn(true)
                .thenReturn(false);

        mockMvc.perform(get("/secure-data"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/secure-data"))
                .andExpect(status().isForbidden());
    }
}
