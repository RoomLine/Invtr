package com.invtr.equipmentservice.exception;

public class EquipmentNotFoundException extends RuntimeException {
    public EquipmentNotFoundException(Long id) {
        super("Equipment not found with id: " + id);
    }
}