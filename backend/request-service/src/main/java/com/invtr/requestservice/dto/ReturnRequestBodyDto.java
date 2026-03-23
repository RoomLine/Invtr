package com.invtr.requestservice.dto;

import com.invtr.requestservice.enums.EquipmentCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Optional body for PUT /requests/{id}/return — condition logged on equipment via equipment-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestBodyDto {
	private Map<Long, EquipmentCondition> conditionPerEquipment;
}