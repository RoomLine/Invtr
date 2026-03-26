package com.invtr.requestservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
	@NotEmpty(message = "At least one equipment ID must be provided")
	private List< @NotNull(message = "Equipment ID cannot be null")
					Long > equipmentIds;

	@NotBlank(message = "fromDate is required")
	@Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "From date must be in YYYY-MM-DD format")
	private String fromDate;

	@NotBlank(message = "toDate is required")
	@Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "To date must be in YYYY-MM-DD format")
	private String toDate;
}
