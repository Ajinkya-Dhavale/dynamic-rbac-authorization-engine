package com.techpulse.rbac.repository;

import com.techpulse.rbac.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, Long> {

    @Query("""
            select count(rp) > 0
            from RolePermission rp
            where rp.permission.name = :permissionName
              and exists (
                  select 1
                  from UserRole ur
                  where ur.role = rp.role
                    and ur.user.username = :username
                    and ur.user.active = true
              )
            """)
    boolean hasPermission(
            @Param("username") String username,
            @Param("permissionName") String permissionName);
}
