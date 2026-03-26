package com.invtr.requestservice.dto;

import com.invtr.requestservice.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestResponse {

	@NotNull(message = "Request ID cannot be null")
	private Long id;

	@NotNull(message = "User ID cannot be null")
	private Long userId;

	@NotNull(message = "Equipment IDs cannot be null")
	@Size(min = 1, message = "At least one equipment ID must be provided")
	private List<@NotNull(message = "Equipment ID cannot be null") Long> equipmentIds;

	@NotNull(message = "Status cannot be null")
	private RequestStatus status;

	@NotNull(message = "Start date and time cannot be null")
	private LocalDateTime startDateTime;

	@NotNull(message = "End date and time cannot be null")
	private LocalDateTime endDateTime;

	@NotNull(message = "Request date cannot be null")
	private LocalDate requestDate;

	private Long approvedBy;

	@NotNull(message = "Created at timestamp cannot be null")
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}