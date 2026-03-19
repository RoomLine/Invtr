package com.invtr.equipmentservice.controller;

import com.invtr.equipmentservice.dto.*;
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
    public ResponseEntity<List<EquipmentResponse>> getAllEquipment() {
        return ResponseEntity.ok("if you're seeing this, the app is working"); // replace body later
    }

    @GetMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EquipmentResponse> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @PostMapping("/equipment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> createEquipment(@Valid @RequestBody CreateEquipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(null); // replace body later
    }

    @PatchMapping("/equipment/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> updateEquipment(
            @Valid @RequestBody UpdateEquipmentRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @PutMapping("/equipment/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> updateEquipmentStatus(
            @Valid @RequestBody UpdateEquipmentStatusRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @DeleteMapping("/equipment/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEquipmentById(@PathVariable Long id) {
        return ResponseEntity.noContent().build(); // replace body later
    }

    @GetMapping("/condition")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ConditionLogResponse>> getAllCondition() {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @GetMapping("/condition/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ConditionLogResponse> getConditionById(@PathVariable Long id) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

}