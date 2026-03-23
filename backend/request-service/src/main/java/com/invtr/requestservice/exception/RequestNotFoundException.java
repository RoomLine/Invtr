package com.invtr.requestservice.exception;

public class RequestNotFoundException extends RuntimeException {
	public RequestNotFoundException(Long id) {
		super("Borrow request not found with id: " + id);
	}
}
