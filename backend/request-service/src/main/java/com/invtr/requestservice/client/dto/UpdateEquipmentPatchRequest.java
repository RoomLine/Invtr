package com.invtr.requestservice.client.dto;

import com.invtr.requestservice.client.enums.EquipmentCondition;
import com.invtr.requestservice.client.enums.EquipmentStatus;
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
