package com.invtr.reportsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryReportResponse {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 100, message = "Username must be at most 100 characters")
    private String username;

    @NotNull(message = "Requests list cannot be null")
    @Size(min = 0, message = "Requests list cannot be negative")
    private List<RequestResponse> requests;
}