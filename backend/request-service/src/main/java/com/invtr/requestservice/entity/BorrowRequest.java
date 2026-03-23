package com.invtr.requestservice.entity;

import com.invtr.requestservice.enums.BorrowRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "user_email", nullable = false)
	private String userEmail;

	@Column(name = "equipment_id", nullable = false)
	private Long equipmentId;

	@Column(name = "equipment_name", nullable = false)
	private String equipmentName;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private BorrowRequestStatus status;

	@Column(name = "borrow_start", nullable = false)
	private LocalDateTime borrowStart;

	@Column(name = "borrow_end", nullable = false)
	private LocalDateTime borrowEnd;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
