package com.invtr.authservice.controller;

import com.invtr.authservice.dto.AuthResponse;
import com.invtr.authservice.dto.LoginRequest;
import com.invtr.authservice.dto.RegisterRequest;
import com.invtr.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/")
    public String home() {
        return "if you're seeing this, the app(AuthController) is working";
    }

    // POST /auth/register
    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        authService.registerUser(request);
    }

    // POST /auth/login
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.loginUser(request);
    }

    @GetMapping("/logout")
    public String logout() {
        // Since we are using JWT, there is so such thing as a session.
        // We just let the frontend know we need to delete the token
        return "Successful logout! Please delete the jwt token."; // we need to change this soon
    }
}