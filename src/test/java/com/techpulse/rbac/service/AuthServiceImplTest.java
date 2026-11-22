package com.techpulse.rbac.service;

import com.techpulse.rbac.constant.AppMessages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techpulse.rbac.dto.LoginRequest;
import com.techpulse.rbac.dto.LoginResponse;
import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.exception.InvalidCredentialsException;
import com.techpulse.rbac.repository.UserRepository;
import com.techpulse.rbac.security.JwtService;
import com.techpulse.rbac.service.impl.AuthServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthServiceImpl authService =
            new AuthServiceImpl(userRepository, passwordEncoder, jwtService);

    @Test
    void returnsTokenForValidCredentials() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword(passwordEncoder.encode("password123"));

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("alice")).thenReturn("signed-token");
        when(jwtService.expirationMs()).thenReturn(3600000L);

        LoginResponse response = authService.login(
                new LoginRequest("alice", "password123"));

        assertEquals("signed-token", response.getAccessToken());
        assertEquals(AppMessages.BEARER_TOKEN_TYPE, response.getTokenType());
        assertEquals(3600000L, response.getExpiresIn());
    }

    @Test
    void rejectsIncorrectPassword() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword(passwordEncoder.encode("password123"));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("alice", "wrongpass")));
    }

    @Test
    void rejectsInactiveUser() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setActive(false);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("alice", "password123")));
    }

    @Test
    void rejectsUnknownUser() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("unknown", "password123")));
    }
}
