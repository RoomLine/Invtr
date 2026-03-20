package com.invtr.equipmentservice.controller;

import com.invtr.equipmentservice.dto.*;
import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import com.invtr.equipmentservice.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equip")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/equipment")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<EquipmentResponse>> getAllEquipment(
            @RequestParam(required = false) EquipmentStatus status,
            @RequestParam(required = false) EquipmentType type,
            @RequestParam(required = false) EquipmentCondition condition) {
        return ResponseEntity.ok(equipmentService.getAllEquipment(status, type, condition));
    }

    @GetMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EquipmentResponse> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @PostMapping("/equipment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> createEquipment(@Valid @RequestBody CreateEquipmentRequest request) {
            equipmentService.createEquipment(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/equipment/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> updateEquipment(@Valid @RequestBody UpdateEquipmentRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.updateEquipmentById(request, id));
    }

    @PatchMapping("/equipment/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> updateEquipmentStatus(
            @Valid @RequestBody UpdateEquipmentStatusRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.updateEquipmentStatus(request, id));
    }

    @DeleteMapping("/equipment/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEquipmentById(@PathVariable Long id) {
        equipmentService.deleteEquipmentById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/condition")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ConditionLogResponse>> getAllCondition(
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) EquipmentCondition condition) {
        return ResponseEntity.ok(equipmentService.getAllConditionLog(equipmentId, condition));
    }

    @GetMapping("/condition/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ConditionLogResponse> getConditionById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getConditionLogById(id));
    }

}