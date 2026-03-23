package com.invtr.requestservice.dto;

import com.invtr.requestservice.enums.BorrowRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRequestResponse {
	private Long id;
	private Long userId;
	private String userEmail;
	private Long equipmentId;
	private String equipmentName;
	private BorrowRequestStatus status;
	private LocalDateTime borrowStart;
	private LocalDateTime borrowEnd;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
