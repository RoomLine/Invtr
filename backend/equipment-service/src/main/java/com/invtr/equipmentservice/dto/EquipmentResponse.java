package com.invtr.equipmentservice.dto;

import jakarta.validation.constraints.NotBlank;
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

    private Long equipmentId;
    private String equipmentName;
    private String equipmentType;
    private String equipmentStatus;
    private String condition;
    private String status;
    private String qrCodeUrl;
    private String location;
    private Boolean isSensitive; // check if we want to include this field
    private LocalDateTime createdAt;

}
