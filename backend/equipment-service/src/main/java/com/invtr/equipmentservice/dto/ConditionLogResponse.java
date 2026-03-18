package com.invtr.equipmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionLogResponse {
    private Long id;
    private Long equipmentId;
    private String condition;
    private String notes;
    private LocalDateTime loggedAt;
}
