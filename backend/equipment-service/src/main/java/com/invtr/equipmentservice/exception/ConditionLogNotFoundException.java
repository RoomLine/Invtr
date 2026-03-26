package com.invtr.equipmentservice.exception;

public class ConditionLogNotFoundException extends RuntimeException {
    public ConditionLogNotFoundException(Long id) {
        super("Condition log not found with id: " + id);
    }
}