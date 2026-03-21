package com.invtr.authservice.dto;

import com.invtr.authservice.validation.AtLeastOneField;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AtLeastOneField(fields = {"email", "firstName", "familyName", "roleName"})
public class UpdateUserRequest {
    @Email(message = "Must be a valid email format")
    private String email;
    private String firstName;
    private String familyName;
    private String role;
}
