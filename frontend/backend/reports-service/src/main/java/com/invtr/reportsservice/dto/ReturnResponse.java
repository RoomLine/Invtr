package com.invtr.reportsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnResponse {

    @NotNull(message = "Equipment ID cannot be null")
    private Long equipmentId;

    @NotBlank(message = "Condition cannot be blank")
    @Size(max = 50, message = "Condition must be at most 50 characters")
    private String condition;

    @NotNull(message = "Return date and time cannot be null")
    private LocalDateTime returnDateTime;
}