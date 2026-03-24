package com.invtr.reportsservice.dto;

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
    private Long id;
    private String email;
    private String firstName;
    private String familyName;
    private String roleName;
    private LocalDateTime createdAt;
}
