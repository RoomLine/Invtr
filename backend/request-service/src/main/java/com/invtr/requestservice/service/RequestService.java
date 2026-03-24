package com.invtr.requestservice.service;

import com.invtr.requestservice.client.EquipmentServiceClient;
import com.invtr.requestservice.dto.EquipmentResponse;
import com.invtr.requestservice.dto.UpdateEquipmentPatchRequest;
import com.invtr.requestservice.entity.RequestItem;
import com.invtr.requestservice.entity.RequestItemId;
import com.invtr.requestservice.entity.Return;
import com.invtr.requestservice.enums.EquipmentCondition;
import com.invtr.requestservice.enums.EquipmentStatus;
import com.invtr.requestservice.dto.RequestResponse;
import com.invtr.requestservice.dto.CreateRequestDto;
import com.invtr.requestservice.dto.ReturnRequestBodyDto;
import com.invtr.requestservice.entity.Request;
import com.invtr.requestservice.enums.RequestStatus;
import com.invtr.requestservice.exception.BadRequestException;
import com.invtr.requestservice.exception.RequestNotFoundException;
import com.invtr.requestservice.repository.RequestRepository;
import com.invtr.requestservice.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

	private final RequestRepository requestRepository;
	private final ReturnRepository returnRepository;
	private final EquipmentServiceClient equipmentServiceClient;

	private static final List<RequestStatus> ACTIVE_STATUSES = List.of(
			RequestStatus.PENDING,
			RequestStatus.APPROVED,
			RequestStatus.TAKEN
	);

	public List<RequestResponse> listForCurrentUser(Long userId) {
		if (userId == null) throw new BadRequestException("Missing user id in token");
		return requestRepository.findByUserIdOrderByCreatedAtDesc(userId)
				.stream().map(this::mapToResponse).toList();
	}

	public List<RequestResponse> listAllForManager() {
		return requestRepository.findAllByOrderByCreatedAtDesc()
				.stream().map(this::mapToResponse).toList();
	}

	@Transactional
	public RequestResponse create(CreateRequestDto dto, Long userId, String authHeader) {
		if (userId == null) throw new BadRequestException("Missing user id in token");

		LocalDateTime start = parseStart(dto.getFromDate());
		LocalDateTime end = parseEnd(dto.getToDate());
		if (end.isBefore(start)) throw new BadRequestException("End date must be after start date");

		boolean needsApproval = false;

		for (Long equipmentId : dto.getEquipmentIds()) {
			EquipmentResponse equipment = equipmentServiceClient.getEquipment(equipmentId, authHeader);

			log.info("Equipment {}: status={}, isSensitive={}", equipmentId, equipment.getStatus(), equipment.getIsSensitive());

			if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
				throw new BadRequestException("Equipment " + equipmentId + " is not available");
			}
			if (requestRepository.existsByEquipmentIdAndStatusIn(equipmentId, ACTIVE_STATUSES)) {
				throw new BadRequestException("Equipment " + equipmentId + " already has an active request");
			}
			if (Boolean.TRUE.equals(equipment.getIsSensitive())) {
				needsApproval = true;
			}
		}

		log.info("needsApproval={}, initialStatus={}", needsApproval, needsApproval ? RequestStatus.PENDING : RequestStatus.APPROVED);


		RequestStatus initialStatus = needsApproval ? RequestStatus.PENDING : RequestStatus.APPROVED;

		Request request = Request.builder()
				.userId(userId)
				.startDateTime(start)
				.endDateTime(end)
				.status(initialStatus)
				.build();

		for (Long equipmentId : dto.getEquipmentIds()) {
			RequestItem item = RequestItem.builder()
					.id(new RequestItemId(0L, equipmentId))
					.request(request)
					.build();
			request.getItems().add(item);
		}

		Request saved = requestRepository.save(request);

		if (saved.getStatus() == RequestStatus.APPROVED) {
			for (Long equipmentId : dto.getEquipmentIds()) {
				equipmentServiceClient.updateEquipmentStatus(equipmentId, EquipmentStatus.CHECKED_OUT, authHeader);
			}
		}

		return mapToResponse(saved);
	}

	@Transactional
	public RequestResponse approve(Long id, Long adminId, String authHeader) {
		Request request = requestRepository.findById(id)
				.orElseThrow(() -> new RequestNotFoundException(id));

		if (request.getStatus() != RequestStatus.PENDING) {
			throw new BadRequestException("Only pending requests can be approved");
		}

		request.setStatus(RequestStatus.APPROVED);
		request.setApprovedBy(adminId);
		requestRepository.save(request);

		for (RequestItem item : request.getItems()) {
			equipmentServiceClient.updateEquipmentStatus(
					item.getId().getEquipmentId(), EquipmentStatus.CHECKED_OUT, authHeader);
		}

		return mapToResponse(request);
	}

	@Transactional
	public RequestResponse reject(Long id) {
		Request request = requestRepository.findById(id)
				.orElseThrow(() -> new RequestNotFoundException(id));

		if (request.getStatus() != RequestStatus.PENDING) {
			throw new BadRequestException("Only pending requests can be rejected");
		}

		request.setStatus(RequestStatus.REJECTED);
		requestRepository.save(request);
		return mapToResponse(request);
	}

	@Transactional
	public RequestResponse processReturn(Long id, ReturnRequestBodyDto body, String authHeader) {
		Request request = requestRepository.findById(id)
				.orElseThrow(() -> new RequestNotFoundException(id));

		if (request.getStatus() != RequestStatus.APPROVED) {
			throw new BadRequestException("Only approved requests can be returned");
		}

		request.setStatus(RequestStatus.RETURNED);
		requestRepository.save(request);

		for (RequestItem item : request.getItems()) {
			Long equipmentId = item.getId().getEquipmentId();

			EquipmentCondition condition = (body != null && body.getConditionPerEquipment() != null)
					? body.getConditionPerEquipment().getOrDefault(equipmentId, EquipmentCondition.GOOD)
					: EquipmentCondition.GOOD;

			Return returnRecord = Return.builder()
					.requestId(request.getId())
					.equipmentId(equipmentId)
					.condition(condition)
					.build();
			returnRepository.save(returnRecord);

			equipmentServiceClient.patchEquipment(equipmentId,
					UpdateEquipmentPatchRequest.builder()
							.status(EquipmentStatus.AVAILABLE)
							.condition(condition)
							.build(), authHeader);
		}

		return mapToResponse(request);
	}

	private RequestResponse mapToResponse(Request request) {
		List<Long> equipmentIds = request.getItems().stream()
				.map(item -> item.getId().getEquipmentId())
				.toList();

		return RequestResponse.builder()
				.id(request.getId())
				.userId(request.getUserId())
				.equipmentIds(equipmentIds)
				.status(request.getStatus())
				.startDateTime(request.getStartDateTime())
				.endDateTime(request.getEndDateTime())
				.requestDate(request.getRequestDate())
				.approvedBy(request.getApprovedBy())
				.createdAt(request.getCreatedAt())
				.updatedAt(request.getUpdatedAt())
				.build();
	}

	private static LocalDateTime parseStart(String raw) {
		String s = raw.trim();
		return s.contains("T") ? LocalDateTime.parse(s) : LocalDate.parse(s).atStartOfDay();
	}

	private static LocalDateTime parseEnd(String raw) {
		String s = raw.trim();
		return s.contains("T") ? LocalDateTime.parse(s) : LocalDate.parse(s).atTime(23, 59, 59);
	}
}