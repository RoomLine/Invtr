package com.invtr.equipmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionAssessmentResponse {
    private String condition;
    private String reasoning;
}