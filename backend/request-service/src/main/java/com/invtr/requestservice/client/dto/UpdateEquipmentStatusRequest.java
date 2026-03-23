package com.invtr.requestservice.client.dto;

import com.invtr.requestservice.client.enums.EquipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEquipmentStatusRequest {
	@NotNull
	private EquipmentStatus status;
}
