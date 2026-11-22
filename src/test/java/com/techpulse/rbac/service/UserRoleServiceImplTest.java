package com.techpulse.rbac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techpulse.rbac.dto.UserRoleResponse;
import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.entity.User;
import com.techpulse.rbac.entity.UserRole;
import com.techpulse.rbac.exception.DuplicateUserRoleException;
import com.techpulse.rbac.exception.ResourceNotFoundException;
import com.techpulse.rbac.repository.RoleRepository;
import com.techpulse.rbac.repository.UserRepository;
import com.techpulse.rbac.repository.UserRoleRepository;
import com.techpulse.rbac.service.impl.UserRoleServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRoleServiceImplTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserRoleRepository userRoleRepository;
    private UserRoleServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        userRoleRepository = mock(UserRoleRepository.class);
        service = new UserRoleServiceImpl(
                userRepository, roleRepository, userRoleRepository);
    }

    @Test
    void assignsRoleSuccessfully() {
        User user = new User();
        user.setId(10L);
        user.setUsername("alice");
        Role role = new Role();
        role.setId(20L);
        role.setName("AUDITOR");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(20L)).thenReturn(Optional.of(role));
        when(userRoleRepository.existsByUserIdAndRoleId(10L, 20L))
                .thenReturn(false);
        when(userRoleRepository.save(any(UserRole.class)))
                .thenAnswer(invocation -> {
                    UserRole mapping = invocation.getArgument(0);
                    mapping.setId(30L);
                    return mapping;
                });

        UserRoleResponse response =
                service.assignRole(10L, 20L);

        assertEquals(30L, response.getId());
        assertEquals(10L, response.getUserId());
        assertEquals(20L, response.getRoleId());
    }

    @Test
    void rejectsDuplicateMapping() {
        User user = new User();
        user.setId(10L);
        Role role = new Role();
        role.setId(20L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(20L)).thenReturn(Optional.of(role));
        when(userRoleRepository.existsByUserIdAndRoleId(10L, 20L))
                .thenReturn(true);

        assertThrows(DuplicateUserRoleException.class,
                () -> service.assignRole(10L, 20L));
    }

    @Test
    void rejectsMissingUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.assignRole(99L, 20L));
    }

    @Test
    void rejectsMissingRole() {
        User user = new User();
        user.setId(10L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.assignRole(10L, 99L));
    }
}
