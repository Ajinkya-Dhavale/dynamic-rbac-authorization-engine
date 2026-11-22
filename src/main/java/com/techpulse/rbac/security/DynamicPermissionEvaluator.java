package com.techpulse.rbac.security;

import com.techpulse.rbac.repository.RolePermissionRepository;
import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class DynamicPermissionEvaluator implements PermissionEvaluator {

    private final RolePermissionRepository rolePermissionRepository;

    public DynamicPermissionEvaluator(
            RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public boolean hasPermission(
            Authentication authentication,
            Object targetDomainObject,
            Object permission) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || permission == null) {
            return false;
        }

        String username = authentication.getName();
        if (username == null || username.isBlank()) {
            return false;
        }

        return rolePermissionRepository.hasPermission(
                username, permission.toString());
    }

    @Override
    public boolean hasPermission(
            Authentication authentication,
            Serializable targetId,
            String targetType,
            Object permission) {
        return hasPermission(authentication, null, permission);
    }
}
