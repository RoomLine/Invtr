package com.invtr.equipmentservice.dto;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import com.invtr.equipmentservice.validation.AtLeastOneField;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AtLeastOneField(fields = {"type", "condition", "location", "status"})
public class UpdateEquipmentRequest {
    @Enumerated(EnumType.STRING)
    private EquipmentType type;
    @Enumerated(EnumType.STRING)
    private EquipmentCondition condition;
    private String location;
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;
}
