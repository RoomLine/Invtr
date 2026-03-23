package com.invtr.requestservice.controller;

import com.invtr.requestservice.dto.BorrowRequestResponse;
import com.invtr.requestservice.dto.CreateBorrowRequestDto;
import com.invtr.requestservice.dto.ReturnBorrowBodyDto;
import com.invtr.requestservice.service.BorrowRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BorrowRequestController {

	private final BorrowRequestService borrowRequestService;

	@GetMapping("/requests")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<List<BorrowRequestResponse>> getMyRequests(
			@RequestAttribute(value = "userId", required = false) Long userId) {
		return ResponseEntity.ok(borrowRequestService.listForCurrentUser(userId));
	}

	@GetMapping("/manager/requests")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<BorrowRequestResponse>> getManagerRequests() {
		return ResponseEntity.ok(borrowRequestService.listAllForManager());
	}

	@PostMapping({"/requests", "/request"})
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<BorrowRequestResponse> createRequest(
			@Valid @RequestBody CreateBorrowRequestDto dto,
			@RequestAttribute(value = "userId", required = false) Long userId,
			Authentication authentication,
			HttpServletRequest request) {
		String email = authentication != null ? authentication.getName() : null;
		String authHeader = request.getHeader("Authorization");
		BorrowRequestResponse created = borrowRequestService.create(dto, userId, email, authHeader);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/requests/{id}/approve")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BorrowRequestResponse> approve(
			@PathVariable Long id,
			HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		return ResponseEntity.ok(borrowRequestService.approve(id, authHeader));
	}

	@PutMapping("/requests/{id}/reject")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BorrowRequestResponse> reject(@PathVariable Long id) {
		return ResponseEntity.ok(borrowRequestService.reject(id));
	}

	@PutMapping("/requests/{id}/return")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BorrowRequestResponse> processReturn(
			@PathVariable Long id,
			@RequestBody(required = false) ReturnBorrowBodyDto body,
			HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		return ResponseEntity.ok(borrowRequestService.processReturn(id, body, authHeader));
	}
}
