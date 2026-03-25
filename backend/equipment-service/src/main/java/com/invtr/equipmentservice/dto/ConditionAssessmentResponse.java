package com.invtr.equipmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionAssessmentResponse {

    @NotBlank(message = "Condition cannot be blank")
    @Pattern(
            regexp = "EXCELLENT|GOOD|DAMAGED|BROKEN",
            message = "Condition must be one of: EXCELLENT, GOOD, DAMAGED, BROKEN"
    )
    private String condition;

    @NotBlank(message = "Reasoning cannot be blank")
    private String reasoning;
}