package com.techpulse.rbac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techpulse.rbac.dto.CreateRoleRequest;
import com.techpulse.rbac.dto.RoleResponse;
import com.techpulse.rbac.entity.Role;
import com.techpulse.rbac.exception.DuplicateRoleException;
import com.techpulse.rbac.repository.RoleRepository;
import com.techpulse.rbac.service.impl.RoleServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleServiceImplTest {

    private RoleRepository roleRepository;
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        roleService = new RoleServiceImpl(roleRepository);
    }

    @Test
    void createsRole() {
        Role savedRole = new Role();
        savedRole.setId(10L);
        savedRole.setName("AUDITOR");
        when(roleRepository.findByNameIgnoreCase("AUDITOR"))
                .thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        RoleResponse response =
                roleService.create(new CreateRoleRequest(" AUDITOR "));

        assertEquals(10L, response.getId());
        assertEquals("AUDITOR", response.getName());
    }

    @Test
    void rejectsDuplicateRole() {
        Role existingRole = new Role();
        existingRole.setName("AUDITOR");
        when(roleRepository.findByNameIgnoreCase("AUDITOR"))
                .thenReturn(Optional.of(existingRole));

        assertThrows(DuplicateRoleException.class,
                () -> roleService.create(
                        new CreateRoleRequest("AUDITOR")));
    }
}
