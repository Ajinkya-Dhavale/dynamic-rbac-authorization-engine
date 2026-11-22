package com.techpulse.rbac.controller;

import com.techpulse.rbac.constant.AppMessages;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techpulse.rbac.dto.LoginRequest;
import com.techpulse.rbac.dto.LoginResponse;
import com.techpulse.rbac.exception.GlobalExceptionHandler;
import com.techpulse.rbac.exception.InvalidCredentialsException;
import com.techpulse.rbac.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerTest {

    private AuthService authService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void loginReturnsToken() throws Exception {
        LoginResponse response =
                new LoginResponse(
                        "signed-token",
                        AppMessages.BEARER_TOKEN_TYPE,
                        3600000L);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("alice", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("signed-token"))
                .andExpect(jsonPath("$.tokenType")
                        .value(AppMessages.BEARER_TOKEN_TYPE));
    }

    @Test
    void loginReturnsUnauthorizedForInvalidCredentials() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("alice", "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void loginReturnsBadRequestForMissingPassword() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"alice\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }
}
