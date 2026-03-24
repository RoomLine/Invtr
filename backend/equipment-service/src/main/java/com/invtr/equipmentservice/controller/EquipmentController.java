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
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<EquipmentResponse>> getAllEquipment(
            @RequestParam(required = false) EquipmentStatus status,
            @RequestParam(required = false) EquipmentType type,
            @RequestParam(required = false) EquipmentCondition condition) {
        return ResponseEntity.ok(equipmentService.getAllEquipment(status, type, condition));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EquipmentResponse> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> createEquipment(@Valid @RequestBody CreateEquipmentRequest request) {
        EquipmentResponse response = equipmentService.createEquipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> updateEquipment(@Valid @RequestBody UpdateEquipmentRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.updateEquipmentById(request, id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> updateEquipmentStatus(
            @Valid @RequestBody UpdateEquipmentStatusRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.updateEquipmentStatus(request, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEquipmentById(@PathVariable Long id) {
        equipmentService.deleteEquipmentById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/condition-logs")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ConditionLogResponse>> getAllCondition(
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) EquipmentCondition condition) {
        return ResponseEntity.ok(equipmentService.getAllConditionLog(equipmentId, condition));
    }

    @GetMapping("/condition-log/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ConditionLogResponse> getConditionById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getConditionLogById(id));
    }

    @PatchMapping("/internal/{id}/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EquipmentResponse> updateStatusInternal(
            @Valid @RequestBody UpdateEquipmentStatusRequest request,
            @PathVariable Long id) {

        return ResponseEntity.ok(equipmentService.updateEquipmentStatus(request, id));
    }

}