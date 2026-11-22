package com.techpulse.rbac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techpulse.rbac.dto.CreateUserRequest;
import com.techpulse.rbac.dto.UserResponse;
import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.exception.DuplicateUserException;
import com.techpulse.rbac.repository.UserRepository;
import com.techpulse.rbac.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void createsUserWithBcryptPasswordAndWithoutPasswordInResponse() {
        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(20L);
                    return user;
                });

        UserResponse response = userService.create(
                new CreateUserRequest("alice", "password123"));

        assertEquals(20L, response.getId());
        assertEquals("alice", response.getUsername());
        assertTrue(response.isActive());

        User savedUser = captureSavedUser();
        assertNotEquals("password123", savedUser.getPassword());
        assertTrue(passwordEncoder.matches(
                "password123", savedUser.getPassword()));
        assertFalse(response.toString().contains("password123"));
        assertFalse(response.toString().contains(savedUser.getPassword()));
    }

    @Test
    void rejectsDuplicateUsername() {
        User existingUser = new User();
        existingUser.setUsername("alice");
        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateUserException.class,
                () -> userService.create(
                        new CreateUserRequest("alice", "password123")));
    }

    private User captureSavedUser() {
        org.mockito.ArgumentCaptor<User> captor =
                org.mockito.ArgumentCaptor.forClass(User.class);
        org.mockito.Mockito.verify(userRepository).save(captor.capture());
        return captor.getValue();
    }
}
