package com.invtr.requestservice.dto;

import com.invtr.requestservice.enums.EquipmentCondition;
import com.invtr.requestservice.enums.EquipmentStatus;
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
	private EquipmentCondition condition;
	private EquipmentStatus status;
}
