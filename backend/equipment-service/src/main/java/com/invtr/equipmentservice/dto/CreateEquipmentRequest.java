package com.invtr.equipmentservice.dto;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEquipmentRequest {
    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    private EquipmentType type;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;
    
    @NotNull(message = "Condition is required")
    @Enumerated(EnumType.STRING)
    private EquipmentCondition condition;
    
    @NotBlank(message = "Location is required")
    private String location;

    private String photoUrl;
}
