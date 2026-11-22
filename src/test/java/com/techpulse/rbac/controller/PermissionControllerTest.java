package com.techpulse.rbac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techpulse.rbac.dto.CreatePermissionRequest;
import com.techpulse.rbac.dto.PermissionResponse;
import com.techpulse.rbac.exception.DuplicatePermissionException;
import com.techpulse.rbac.exception.GlobalExceptionHandler;
import com.techpulse.rbac.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PermissionControllerTest {

    private PermissionService permissionService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        permissionService = mock(PermissionService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new PermissionController(permissionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createsPermissionSuccessfully() throws Exception {
        when(permissionService.create(any(CreatePermissionRequest.class)))
                .thenReturn(
                        new PermissionResponse(30L, "EXPORT_REPORT"));

        mockMvc.perform(post("/permissions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreatePermissionRequest(
                                        "EXPORT_REPORT"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.name").value("EXPORT_REPORT"));
    }

    @Test
    void rejectsDuplicatePermission() throws Exception {
        when(permissionService.create(any(CreatePermissionRequest.class)))
                .thenThrow(new DuplicatePermissionException());

        mockMvc.perform(post("/permissions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreatePermissionRequest(
                                        "EXPORT_REPORT"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void rejectsInvalidPermissionName() throws Exception {
        mockMvc.perform(post("/permissions")
                        .contentType("application/json")
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }
}
