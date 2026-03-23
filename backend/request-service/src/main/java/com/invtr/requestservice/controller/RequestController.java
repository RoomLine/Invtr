package com.invtr.requestservice.controller;

import com.invtr.requestservice.dto.RequestResponse;
import com.invtr.requestservice.dto.CreateRequestDto;
import com.invtr.requestservice.dto.ReturnRequestBodyDto;
import com.invtr.requestservice.service.RequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/requests")
@RestController
@RequiredArgsConstructor
public class RequestController {

	private final RequestService borrowRequestService;

	@GetMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<List<RequestResponse>> getMyRequests(
			@RequestAttribute(value = "userId", required = false) Long userId) {
		return ResponseEntity.ok(borrowRequestService.listForCurrentUser(userId));
	}

	@GetMapping("/manager")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<RequestResponse>> getManagerRequests() {
		return ResponseEntity.ok(borrowRequestService.listAllForManager());
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<RequestResponse> createRequest(
			@Valid @RequestBody CreateRequestDto dto,
			@RequestAttribute(value = "userId", required = false) Long userId,
			HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		RequestResponse created = borrowRequestService.create(dto, userId, authHeader);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}/approve")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RequestResponse> approve(
			@PathVariable Long id,
			@RequestAttribute(value = "userId", required = false) Long adminId,
			HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		return ResponseEntity.ok(borrowRequestService.approve(id, adminId, authHeader));
	}

	@PutMapping("/{id}/reject")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RequestResponse> reject(@PathVariable Long id) {
		return ResponseEntity.ok(borrowRequestService.reject(id));
	}

	@PutMapping("/{id}/return")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RequestResponse> processReturn(
			@PathVariable Long id,
			@RequestBody(required = false) ReturnRequestBodyDto body,
			HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		return ResponseEntity.ok(borrowRequestService.processReturn(id, body, authHeader));
	}
}