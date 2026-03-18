package com.invtr.equipmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEquipmentRequest {
    private String type;
    private String status;
    private String condition;
    private String location;
    private Boolean isSensitive;
}
