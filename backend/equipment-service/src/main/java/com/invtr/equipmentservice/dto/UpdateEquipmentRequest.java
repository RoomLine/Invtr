package com.invtr.equipmentservice.dto;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import com.invtr.equipmentservice.validation.AtLeastOneField;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
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

    @Size(max = 128, message = "Location must be at most 128 characters")
    private String location;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;
}