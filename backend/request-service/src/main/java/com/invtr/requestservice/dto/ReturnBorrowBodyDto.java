package com.invtr.requestservice.dto;

import com.invtr.requestservice.client.enums.EquipmentCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Optional body for PUT /requests/{id}/return — condition logged on equipment via equipment-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnBorrowBodyDto {
	private EquipmentCondition conditionOnReturn;
}
