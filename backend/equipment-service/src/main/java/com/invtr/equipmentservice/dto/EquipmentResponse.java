package com.invtr.equipmentservice.dto;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentResponse {

    private Long id;
    private String name;
    private EquipmentStatus status;
    private EquipmentCondition condition;
    private EquipmentType type;
    private String qrCodeUrl;
    private String location;
    private Boolean isSensitive; // check if we want to include this field
    private LocalDateTime createdAt;
    private String photoUrl;

}
