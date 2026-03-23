package com.invtr.requestservice.exception;

public class BorrowRequestNotFoundException extends RuntimeException {
	public BorrowRequestNotFoundException(Long id) {
		super("Borrow request not found with id: " + id);
	}
}
