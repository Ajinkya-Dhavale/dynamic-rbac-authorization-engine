package com.techpulse.rbac.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.techpulse.rbac.repository.RolePermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

class DynamicPermissionEvaluatorTest {

    private RolePermissionRepository rolePermissionRepository;
    private DynamicPermissionEvaluator evaluator;

    @BeforeEach
    void setUp() {
        rolePermissionRepository = mock(RolePermissionRepository.class);
        evaluator = new DynamicPermissionEvaluator(rolePermissionRepository);
    }

    @Test
    void grantsDatabasePermission() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin", null, AuthorityUtils.NO_AUTHORITIES);
        when(rolePermissionRepository.hasPermission(
                "admin", "MANAGE_ROLES")).thenReturn(true);

        assertTrue(evaluator.hasPermission(
                authentication, null, "MANAGE_ROLES"));
    }

    @Test
    void deniesMissingDatabasePermission() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin", null, AuthorityUtils.NO_AUTHORITIES);
        when(rolePermissionRepository.hasPermission(
                "admin", "READ_SECURE_DATA")).thenReturn(false);

        assertFalse(evaluator.hasPermission(
                authentication, null, "READ_SECURE_DATA"));
    }

    @Test
    void deniesUnauthenticatedUser() {
        AnonymousAuthenticationToken anonymous =
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymous",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        assertFalse(evaluator.hasPermission(
                anonymous, null, "MANAGE_ROLES"));
    }

    @Test
    void deniesNullAuthentication() {
        assertFalse(evaluator.hasPermission(
                null, null, "MANAGE_ROLES"));
    }
}
