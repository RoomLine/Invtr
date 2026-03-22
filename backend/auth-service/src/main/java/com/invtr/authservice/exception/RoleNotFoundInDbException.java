package com.invtr.authservice.exception;

public class RoleNotFoundInDbException extends RuntimeException {
    public RoleNotFoundInDbException(String roleName) {
        super("Role '" + roleName + "' not found in the database.");
    }
}
