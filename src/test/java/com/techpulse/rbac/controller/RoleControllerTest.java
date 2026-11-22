package com.techpulse.rbac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techpulse.rbac.dto.CreateRoleRequest;
import com.techpulse.rbac.dto.RoleResponse;
import com.techpulse.rbac.exception.DuplicateRoleException;
import com.techpulse.rbac.exception.GlobalExceptionHandler;
import com.techpulse.rbac.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RoleControllerTest {

    private RoleService roleService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        roleService = mock(RoleService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new RoleController(roleService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createsRoleSuccessfully() throws Exception {
        when(roleService.create(any(CreateRoleRequest.class)))
                .thenReturn(new RoleResponse(10L, "AUDITOR"));

        mockMvc.perform(post("/roles")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreateRoleRequest("AUDITOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("AUDITOR"));
    }

    @Test
    void rejectsDuplicateRole() throws Exception {
        when(roleService.create(any(CreateRoleRequest.class)))
                .thenThrow(new DuplicateRoleException());

        mockMvc.perform(post("/roles")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreateRoleRequest("AUDITOR"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void rejectsInvalidRoleName() throws Exception {
        mockMvc.perform(post("/roles")
                        .contentType("application/json")
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }
}
