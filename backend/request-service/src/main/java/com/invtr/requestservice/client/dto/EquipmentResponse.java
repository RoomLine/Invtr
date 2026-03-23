package com.invtr.requestservice.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.invtr.requestservice.client.enums.EquipmentCondition;
import com.invtr.requestservice.client.enums.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentResponse {
	private Long id;
	private String name;
	private EquipmentStatus status;
	private EquipmentCondition condition;
	private String qrCodeUrl;
	private String location;
	private Boolean isSensitive;
	private LocalDateTime createdAt;
	private String photoUrl;
}
