package com.invtr.requestservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRequestDto {
	@NotNull(message = "equipmentId is required")
	private List<Long> equipmentIds;

	@NotBlank(message = "fromDate is required")
	private String fromDate;

	@NotBlank(message = "toDate is required")
	private String toDate;
}
