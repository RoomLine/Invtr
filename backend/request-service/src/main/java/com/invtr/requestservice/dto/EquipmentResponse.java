package com.invtr.requestservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.invtr.requestservice.enums.EquipmentCondition;
import com.invtr.requestservice.enums.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentResponse {

	@NotNull(message = "Equipment ID cannot be null")
	private Long id;

	@NotBlank(message = "Name cannot be blank")
	@Size(max = 100, message = "Name must be at most 100 characters")
	private String name;

	@NotNull(message = "Status cannot be null")
	private EquipmentStatus status;

	@NotNull(message = "Condition cannot be null")
	private EquipmentCondition condition;

	@Size(max = 255, message = "QR code URL must be at most 255 characters")
	private String qrCodeUrl;

	@NotBlank(message = "Location cannot be blank")
	@Size(max = 100, message = "Location must be at most 100 characters")
	private String location;

//	@NotNull(message = "Sensitive flag cannot be null")
	private Boolean isSensitive; // TODO: figure out if we want this

	@NotNull(message = "Created at timestamp cannot be null")
	private LocalDateTime createdAt;

	@Size(max = 255, message = "Photo URL must be at most 255 characters")
	private String photoUrl;
}
