package com.invtr.equipmentservice.controller;


import com.invtr.equipmentservice.entity.Equipment;
import com.invtr.equipmentservice.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/equipment")
    public ResponseEntity<String> getAllEquipment() {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @GetMapping("/equipment/{id}")
    public ResponseEntity<String> getEquipmentById(@RequestParam("id") String id) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @PostMapping("/setEquipment")
    public ResponseEntity<String> setEquipment(@Valid @RequestBody Equipment equipment) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @PutMapping("/equipment/{id}")
    public ResponseEntity<String> updateEquipment(@Valid @RequestBody Equipment equipment, @PathVariable String id) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @PutMapping("/equipment/{id}/status")
    public ResponseEntity<String> updateEquipmentStatus(@Valid @RequestBody Equipment equipment, @PathVariable String id, String status) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }

    @DeleteMapping("/equipment/{id}")
    public ResponseEntity<String> deleteEquipment(@Valid @RequestBody Equipment equipment, @PathVariable String id) {
        return ResponseEntity.ok("if you're seeing this, the app is working");
    }
}
