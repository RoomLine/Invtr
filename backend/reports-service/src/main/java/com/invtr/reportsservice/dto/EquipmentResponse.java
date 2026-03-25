package com.invtr.reportsservice.dto;

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
    private String status;
    private String condition;
    private String type;
    private String qrCodeUrl;
    private String location;
    private Boolean isSensitive; // check if we want to include this field
    private LocalDateTime createdAt;
    private String photoUrl;

}
