package com.invtr.authservice.dto;

import com.invtr.authservice.validation.AtLeastOneField;
import jakarta.validation.constraints.Email;
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
@AtLeastOneField(fields = {"email", "firstName", "familyName", "role"})
public class UpdateUserRequest {

    @Email(message = "Must be a valid email format")
    private String email;

    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @Size(max = 50, message = "Family name must be at most 50 characters")
    private String familyName;

    @Size(max = 16, message = "Role must be at most 16 characters")
    private String role;
}
