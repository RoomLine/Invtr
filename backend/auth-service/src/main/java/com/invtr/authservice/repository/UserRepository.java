package com.invtr.authservice.repository;

import com.invtr.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email); // Quick check to prevent duplicate registrations

    @Query("SELECT u FROM User u WHERE u.role.id = :roleId")
    List<User> findByRole(@Param("roleId") int roleId);
}