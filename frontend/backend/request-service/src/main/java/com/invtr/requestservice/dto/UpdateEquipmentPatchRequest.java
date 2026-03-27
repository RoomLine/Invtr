package com.invtr.requestservice.dto;

import com.invtr.requestservice.enums.EquipmentCondition;
import com.invtr.requestservice.enums.EquipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Subset of equipment-service UpdateEquipmentRequest for PATCH /equipment/{id}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEquipmentPatchRequest {

	@NotNull(message = "Equipment condition cannot be null")
	private EquipmentCondition condition;

	@NotNull(message = "Equipment condition cannot be null")
	private EquipmentStatus status;
}
