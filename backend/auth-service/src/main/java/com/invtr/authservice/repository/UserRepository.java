package com.invtr.authservice.repository;

import com.invtr.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // Quick check to prevent duplicate registrations
}