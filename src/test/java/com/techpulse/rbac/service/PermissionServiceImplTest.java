package com.techpulse.rbac.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techpulse.rbac.dto.CreatePermissionRequest;
import com.techpulse.rbac.dto.PermissionResponse;
import com.techpulse.rbac.entity.Permission;
import com.techpulse.rbac.exception.DuplicatePermissionException;
import com.techpulse.rbac.repository.PermissionRepository;
import com.techpulse.rbac.service.impl.PermissionServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionServiceImplTest {

    private PermissionRepository permissionRepository;
    private PermissionServiceImpl permissionService;

    @BeforeEach
    void setUp() {
        permissionRepository = mock(PermissionRepository.class);
        permissionService =
                new PermissionServiceImpl(permissionRepository);
    }

    @Test
    void createsPermission() {
        Permission savedPermission = new Permission();
        savedPermission.setId(30L);
        savedPermission.setName("EXPORT_REPORT");
        when(permissionRepository.findByNameIgnoreCase("EXPORT_REPORT"))
                .thenReturn(Optional.empty());
        when(permissionRepository.save(any(Permission.class)))
                .thenReturn(savedPermission);

        PermissionResponse response = permissionService.create(
                new CreatePermissionRequest(" EXPORT_REPORT "));

        assertEquals(30L, response.getId());
        assertEquals("EXPORT_REPORT", response.getName());
    }

    @Test
    void rejectsDuplicatePermission() {
        Permission existingPermission = new Permission();
        existingPermission.setName("EXPORT_REPORT");
        when(permissionRepository.findByNameIgnoreCase("EXPORT_REPORT"))
                .thenReturn(Optional.of(existingPermission));

        assertThrows(DuplicatePermissionException.class,
                () -> permissionService.create(
                        new CreatePermissionRequest("EXPORT_REPORT")));
    }
}
