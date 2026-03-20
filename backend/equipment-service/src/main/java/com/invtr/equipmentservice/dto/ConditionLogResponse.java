package com.invtr.equipmentservice.dto;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private EquipmentCondition condition;
    private LocalDateTime loggedAt;
}
