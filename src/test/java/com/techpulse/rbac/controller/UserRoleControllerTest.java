package com.techpulse.rbac.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techpulse.rbac.dto.UserRoleResponse;
import com.techpulse.rbac.exception.DuplicateUserRoleException;
import com.techpulse.rbac.exception.GlobalExceptionHandler;
import com.techpulse.rbac.service.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UserRoleControllerTest {

    private UserRoleService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(UserRoleService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new UserRoleController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void assignsRoleSuccessfully() throws Exception {
        when(service.assignRole(10L, 20L))
                .thenReturn(new UserRoleResponse(30L, 10L, 20L));

        mockMvc.perform(post("/users/10/roles/20"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.roleId").value(20));
    }

    @Test
    void rejectsInvalidIds() throws Exception {
        mockMvc.perform(post("/users/0/roles/20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsDuplicateMapping() throws Exception {
        when(service.assignRole(10L, 20L))
                .thenThrow(new DuplicateUserRoleException());

        mockMvc.perform(post("/users/10/roles/20"))
                .andExpect(status().isConflict());
    }
}
