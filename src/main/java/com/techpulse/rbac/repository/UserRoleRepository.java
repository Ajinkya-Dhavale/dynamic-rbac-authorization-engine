package com.techpulse.rbac.repository;

import com.techpulse.rbac.entity.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("""
            select ur
            from UserRole ur
            where ur.user.username = :username
            """)
    List<UserRole> findByUsername(@Param("username") String username);
}
