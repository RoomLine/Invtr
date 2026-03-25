package com.invtr.equipmentservice.dto;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Equipment ID cannot be null")
    private Long equipmentId;

    @NotNull(message = "Condition cannot be null")
    @Enumerated(EnumType.STRING)
    private EquipmentCondition condition;

    @NotNull(message = "Logged timestamp cannot be null")
    private LocalDateTime loggedAt;
}