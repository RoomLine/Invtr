package com.invtr.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponse {

    @NotNull(message = "User ID cannot be null")
    private Long id;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Must be a valid email format")
    private String email;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Family name cannot be blank")
    @Size(max = 50, message = "Family name must be at most 50 characters")
    private String familyName;

    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 20, message = "Role name must be at most 20 characters")
    private String roleName;

    @NotNull(message = "Created at timestamp cannot be null")
    private LocalDateTime createdAt;
}
