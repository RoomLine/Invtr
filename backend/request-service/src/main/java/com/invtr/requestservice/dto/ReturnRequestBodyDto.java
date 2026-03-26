package com.invtr.requestservice.dto;

import com.invtr.requestservice.enums.EquipmentCondition;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "Condition per equipment map cannot be null")
	@NotEmpty(message = "Condition per equipment map cannot be empty")
	private Map< @NotNull(message = "Equipment ID cannot be null") Long,
					@NotNull(message = "Equipment condition cannot be null") EquipmentCondition >
			conditionPerEquipment;
}