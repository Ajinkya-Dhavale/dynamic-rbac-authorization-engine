package com.techpulse.rbac.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techpulse.rbac.dto.RolePermissionResponse;
import com.techpulse.rbac.exception.GlobalExceptionHandler;
import com.techpulse.rbac.exception.DuplicateRolePermissionException;
import com.techpulse.rbac.service.RolePermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RolePermissionControllerTest {

    private RolePermissionService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(RolePermissionService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new RolePermissionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void assignsPermissionSuccessfully() throws Exception {
        when(service.assignPermission(1L, 2L))
                .thenReturn(new RolePermissionResponse(3L, 1L, 2L));

        mockMvc.perform(post("/roles/1/permissions/2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.roleId").value(1))
                .andExpect(jsonPath("$.permissionId").value(2));
    }

    @Test
    void rejectsInvalidIds() throws Exception {
        mockMvc.perform(post("/roles/0/permissions/2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsDuplicateMapping() throws Exception {
        when(service.assignPermission(1L, 2L))
                .thenThrow(new DuplicateRolePermissionException());

        mockMvc.perform(post("/roles/1/permissions/2"))
                .andExpect(status().isConflict());
    }
}
