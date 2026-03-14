package com.invtr.authservice.service;

import com.invtr.authservice.dto.AuthResponse;
import com.invtr.authservice.dto.LoginRequest;
import com.invtr.authservice.dto.RegisterRequest;
import com.invtr.authservice.entity.Role;
import com.invtr.authservice.entity.User;
import com.invtr.authservice.repository.RoleRepository;
import com.invtr.authservice.repository.UserRepository;
import com.invtr.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Инжектираме инструмента за криптиране
    private final JwtService jwtService;

    public void registerUser(RegisterRequest request) {

        // СТЪПКА 1: Проверка за дубликати
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Този имейл вече е регистриран!");
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Грешка: Ролята 'USER' не е намерена в базата данни."));

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
                .orElseThrow(() -> new RuntimeException("Грешен имейл или парола!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Грешен имейл или парола!");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}