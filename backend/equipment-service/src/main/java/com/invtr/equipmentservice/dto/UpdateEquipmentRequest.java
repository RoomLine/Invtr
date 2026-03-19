package com.invtr.equipmentservice.dto;

import com.invtr.equipmentservice.validation.AtLeastOneField;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AtLeastOneField(fields = {"type", "condition", "location"})
public class UpdateEquipmentRequest {
    private String type;
    private String condition;
    private String location;
}
