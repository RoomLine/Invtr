package com.invtr.equipmentservice.dto;

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
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotBlank(message = "Serial number is required")
    private String serialNumber;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    @NotBlank(message = "Condition is required")
    private String condition;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private Boolean isSensitive;
}
