package com.invtr.requestservice.service;

import com.invtr.requestservice.client.dto.EquipmentResponse;
import com.invtr.requestservice.client.dto.UpdateEquipmentPatchRequest;
import com.invtr.requestservice.client.enums.EquipmentStatus;
import com.invtr.requestservice.dto.BorrowRequestResponse;
import com.invtr.requestservice.dto.CreateBorrowRequestDto;
import com.invtr.requestservice.dto.ReturnBorrowBodyDto;
import com.invtr.requestservice.entity.BorrowRequest;
import com.invtr.requestservice.enums.BorrowRequestStatus;
import com.invtr.requestservice.exception.BadRequestException;
import com.invtr.requestservice.exception.BorrowRequestNotFoundException;
import com.invtr.requestservice.repository.BorrowRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRequestService {

	private final BorrowRequestRepository borrowRequestRepository;
	private final EquipmentServiceClient equipmentServiceClient;

	private static final List<BorrowRequestStatus> ACTIVE_STATUSES = List.of(
			BorrowRequestStatus.PENDING,
			BorrowRequestStatus.APPROVED
	);

	public List<BorrowRequestResponse> listForCurrentUser(Long userId) {
		if (userId == null) {
			throw new BadRequestException("Missing user id in token");
		}
		return borrowRequestRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
				.map(this::mapToResponse)
				.toList();
	}

	public List<BorrowRequestResponse> listAllForManager() {
		return borrowRequestRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Transactional
	public BorrowRequestResponse create(CreateBorrowRequestDto dto, Long userId, String userEmail, String authHeader) {
		if (userId == null) {
			throw new BadRequestException("Missing user id in token");
		}

		EquipmentResponse equipment = equipmentServiceClient.getEquipment(dto.getEquipmentId(), authHeader);

		if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
			throw new BadRequestException("Equipment is not available");
		}

		if (borrowRequestRepository.existsByEquipmentIdAndStatusIn(dto.getEquipmentId(), ACTIVE_STATUSES)) {
			throw new BadRequestException("This equipment already has an active borrow request");
		}

		LocalDateTime start = parseStart(dto.getFromDate());
		LocalDateTime end = parseEnd(dto.getToDate());
		if (end.isBefore(start)) {
			throw new BadRequestException("End date must be after start date");
		}

		BorrowRequestStatus initialStatus = Boolean.TRUE.equals(equipment.getIsSensitive())
				? BorrowRequestStatus.PENDING
				: BorrowRequestStatus.APPROVED;

		BorrowRequest entity = BorrowRequest.builder()
				.userId(userId)
				.userEmail(userEmail != null ? userEmail : "")
				.equipmentId(dto.getEquipmentId())
				.equipmentName(equipment.getName() != null ? equipment.getName() : "Equipment #" + dto.getEquipmentId())
				.status(initialStatus)
				.borrowStart(start)
				.borrowEnd(end)
				.build();

		BorrowRequest saved = borrowRequestRepository.save(entity);

		if (saved.getStatus() == BorrowRequestStatus.APPROVED) {
			equipmentServiceClient.updateEquipmentStatus(saved.getEquipmentId(), EquipmentStatus.CHECKED_OUT, authHeader);
		}

		return mapToResponse(saved);
	}

	@Transactional
	public BorrowRequestResponse approve(Long id, String authHeader) {
		BorrowRequest br = borrowRequestRepository.findById(id)
				.orElseThrow(() -> new BorrowRequestNotFoundException(id));

		if (br.getStatus() != BorrowRequestStatus.PENDING) {
			throw new BadRequestException("Only pending requests can be approved");
		}

		EquipmentResponse equipment = equipmentServiceClient.getEquipment(br.getEquipmentId(), authHeader);
		if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
			throw new BadRequestException("Equipment is not available for checkout");
		}

		br.setStatus(BorrowRequestStatus.APPROVED);
		borrowRequestRepository.save(br);

		equipmentServiceClient.updateEquipmentStatus(br.getEquipmentId(), EquipmentStatus.CHECKED_OUT, authHeader);

		return mapToResponse(br);
	}

	@Transactional
	public BorrowRequestResponse reject(Long id) {
		BorrowRequest br = borrowRequestRepository.findById(id)
				.orElseThrow(() -> new BorrowRequestNotFoundException(id));

		if (br.getStatus() != BorrowRequestStatus.PENDING) {
			throw new BadRequestException("Only pending requests can be rejected");
		}

		br.setStatus(BorrowRequestStatus.REJECTED);
		borrowRequestRepository.save(br);

		return mapToResponse(br);
	}

	@Transactional
	public BorrowRequestResponse processReturn(Long id, ReturnBorrowBodyDto body, String authHeader) {
		BorrowRequest br = borrowRequestRepository.findById(id)
				.orElseThrow(() -> new BorrowRequestNotFoundException(id));

		if (br.getStatus() != BorrowRequestStatus.APPROVED) {
			throw new BadRequestException("Only approved (checked-out) requests can be returned");
		}

		br.setStatus(BorrowRequestStatus.RETURNED);
		borrowRequestRepository.save(br);

		if (body != null && body.getConditionOnReturn() != null) {
			equipmentServiceClient.patchEquipment(
					br.getEquipmentId(),
					UpdateEquipmentPatchRequest.builder()
							.status(EquipmentStatus.AVAILABLE)
							.condition(body.getConditionOnReturn())
							.build(),
					authHeader
			);
		} else {
			equipmentServiceClient.updateEquipmentStatus(br.getEquipmentId(), EquipmentStatus.AVAILABLE, authHeader);
		}

		return mapToResponse(br);
	}

	private BorrowRequestResponse mapToResponse(BorrowRequest br) {
		return BorrowRequestResponse.builder()
				.id(br.getId())
				.userId(br.getUserId())
				.userEmail(br.getUserEmail())
				.equipmentId(br.getEquipmentId())
				.equipmentName(br.getEquipmentName())
				.status(br.getStatus())
				.borrowStart(br.getBorrowStart())
				.borrowEnd(br.getBorrowEnd())
				.createdAt(br.getCreatedAt())
				.updatedAt(br.getUpdatedAt())
				.build();
	}

	private static LocalDateTime parseStart(String raw) {
		String s = raw.trim();
		if (s.contains("T")) {
			return LocalDateTime.parse(s);
		}
		return LocalDate.parse(s).atStartOfDay();
	}

	private static LocalDateTime parseEnd(String raw) {
		String s = raw.trim();
		if (s.contains("T")) {
			return LocalDateTime.parse(s);
		}
		return LocalDate.parse(s).atTime(23, 59, 59);
	}
}
