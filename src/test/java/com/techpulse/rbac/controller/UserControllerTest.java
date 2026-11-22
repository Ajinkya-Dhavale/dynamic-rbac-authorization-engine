package com.techpulse.rbac.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techpulse.rbac.dto.CreateUserRequest;
import com.techpulse.rbac.dto.UserResponse;
import com.techpulse.rbac.exception.DuplicateUserException;
import com.techpulse.rbac.exception.GlobalExceptionHandler;
import com.techpulse.rbac.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UserControllerTest {

    private UserService userService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createsUserSuccessfully() throws Exception {
        when(userService.create(any(CreateUserRequest.class)))
                .thenReturn(new UserResponse(20L, "alice", true));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest(
                                        "alice", "password123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void rejectsDuplicateUsername() throws Exception {
        when(userService.create(any(CreateUserRequest.class)))
                .thenThrow(new DuplicateUserException());

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest(
                                        "alice", "password123"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"username\":\"\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.username").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }
}
