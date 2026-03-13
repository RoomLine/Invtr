package com.invtr.authservice.repository;

import com.invtr.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Spring magicheski shte napravi tova v: SELECT * FROM roles WHERE role_name = ?
    Optional<Role> findByRoleName(String roleName);
}