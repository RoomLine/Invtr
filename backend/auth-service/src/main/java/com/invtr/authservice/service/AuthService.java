package com.invtr.authservice.service;

import com.invtr.authservice.dto.*;
import com.invtr.authservice.entity.Role;
import com.invtr.authservice.entity.User;
import com.invtr.authservice.exception.EmailAlreadyExistsException;
import com.invtr.authservice.exception.RoleNotFoundException;
import com.invtr.authservice.exception.UserNotFoundException;
import com.invtr.authservice.repository.RoleRepository;
import com.invtr.authservice.repository.UserRepository;
import com.invtr.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Инжектираме инструмента за криптиране
    private final JwtService jwtService;

    public UsersResponse mapToUsersResponse(User user) {
        return UsersResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .familyName(user.getFamilyName())
                .roleName(user.getRole().getRoleName())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public void registerUser(RegisterRequest request) {

        // СТЪПКА 1: Проверка за дубликати
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RoleNotFoundException("USER"));

        User newUser = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // Тук криптираме!
                .firstName(request.getFirstName())
                .familyName(request.getFamilyName())
                .role(userRole) // Закачаме намерената роля
                .build();

        userRepository.save(newUser);
    }

    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password!");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public List<String> getAllAdminEmails() {
        return userRepository.findAll()
                .stream()
                .filter(user -> "ADMIN".equals(user.getRole().getRoleName()))
                .map(User::getEmail)
                .toList();
    }

    public List<UsersResponse> getAllUsers(String firstName, String familyName, String email, String roleName) {
        return userRepository.findAll()
                .stream()
                .filter(u -> firstName == null || u.getFirstName().equalsIgnoreCase(firstName))
                .filter(u -> familyName == null || u.getFamilyName().equalsIgnoreCase(familyName))
                .filter(u -> email == null || u.getEmail().equalsIgnoreCase(email))
                .filter(u -> roleName == null || u.getRole().getRoleName().equalsIgnoreCase(roleName))
                .map(this::mapToUsersResponse)
                .toList();
    }

    public UsersResponse updateUserById(UpdateUserRequest request, long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getFirstName() != null) {
            existing.setFirstName(request.getFirstName());
        }
        if (request.getFamilyName() != null) {
            existing.setFamilyName(request.getFamilyName());
        }
        if (request.getEmail() != null) {
            existing.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            Role role = roleRepository.findByRoleName(request.getRole())
                    .orElseThrow(() -> new RoleNotFoundException(request.getRole()));
            existing.setRole(role);
        }

        User updated = userRepository.save(existing);
        return mapToUsersResponse(updated);
    }
}