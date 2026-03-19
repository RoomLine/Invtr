package com.invtr.equipmentservice.service;


import com.invtr.equipmentservice.dto.*;
import com.invtr.equipmentservice.entity.ConditionLog;
import com.invtr.equipmentservice.entity.Equipment;
import com.invtr.equipmentservice.repository.ConditionLogRepository;
import com.invtr.equipmentservice.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final ConditionLogRepository conditionLogRepository;
    private final StockService stockService;

    private EquipmentResponse mapToResponse(Equipment equipment) {
        return EquipmentResponse.builder()
                .equipmentId(equipment.getId())
                .equipmentName(equipment.getName()) // Ensure you added 'name' to Equipment.java!
                .equipmentType(equipment.getType())
                .equipmentStatus(equipment.getStatus())
                .condition(equipment.getCondition())
                .qrCodeUrl(equipment.getQrCodeUrl())
                .location(equipment.getLocation())
                .isSensitive(equipment.getIsSensitive())
                .createdAt(equipment.getCreatedAt())
                .build();
    }

    private ConditionLogResponse mapToConditionLogResponse(ConditionLog conditionLog) {
        return ConditionLogResponse.builder()
                .id(conditionLog.getId())
                .equipmentId(conditionLog.getEquipmentId())
                .condition(conditionLog.getCondition())
                .notes(conditionLog.getNotes())
                .loggedAt(conditionLog.getLoggedAt())
                .build();
    }

    public EquipmentResponse createEquipment(CreateEquipmentRequest request) {
        String generatedQrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="
                + request.getSerialNumber();

        // 2. Apply Business Rule: Only Electrical is sensitive
        boolean isElectrical = "Electrical".equalsIgnoreCase(request.getType());

        // 3. Map to Entity (isSensitive is calculated, not pulled from request)
        Equipment newEquipment = Equipment.builder()
                .name(request.getName())
                .type(request.getType())
                .serialNumber(request.getSerialNumber())
                .status(request.getStatus())
                .condition(request.getCondition())
                .location(request.getLocation())
                .qrCodeUrl(generatedQrUrl)
                .isSensitive(isElectrical) // Handled by backend logic
                .build();

        Equipment saved = equipmentRepository.save(newEquipment);

        // 4. Return the response (Frontend sees the calculated sensitivity)
        return mapToResponse(saved);
    }

    public List<EquipmentResponse> getAllEquipment() {
        return equipmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EquipmentResponse getEquipmentById(Long id) {
        return equipmentRepository.findById(id) // 1. Use the inherited method
                .map(this::mapToResponse)       // 2. If found, convert to DTO using your helper
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id)); // 3. If not found, crash gracefully
    }

    public void deleteEquipmentById(Long id) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
        equipmentRepository.deleteById(id);
        stockService.checkStockAndNotify(existing.getName(), existing.getType());
    }

    public EquipmentResponse updateEquipmentById(UpdateEquipmentRequest updateRequest, Long id) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found equipment with id: " + id));

        if (updateRequest.getLocation() != null) {
            existing.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getCondition() != null) {
            existing.setCondition(updateRequest.getCondition());
        }
        if (updateRequest.getType() != null) {
            existing.setType(updateRequest.getType());
            existing.setIsSensitive("Electrical".equalsIgnoreCase(updateRequest.getType()));
        }

        Equipment saved = equipmentRepository.save(existing);

        if ("Broken".equalsIgnoreCase(saved.getCondition())) {
            stockService.checkStockAndNotify(saved.getName(), saved.getType());
        }

        return mapToResponse(saved);
    }

    public EquipmentResponse updateEquipmentStatus(UpdateEquipmentStatusRequest updateStatusRequest, Long id) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found equipment with id: " + id));

        String oldStatus = existing.getStatus();
        String newStatus = updateStatusRequest.getStatus();

        existing.setStatus(newStatus);
        Equipment saved = equipmentRepository.save(existing);

        boolean statusChangedToUnavailable =
                ("Checked Out".equalsIgnoreCase(newStatus) ||
                        "Under Repair".equalsIgnoreCase(newStatus) ||
                        "Retired".equalsIgnoreCase(newStatus)) &&
                        !newStatus.equalsIgnoreCase(oldStatus);

        if (statusChangedToUnavailable) {
            stockService.checkStockAndNotify(existing.getName(), existing.getType());
        }

        return mapToResponse(saved);
    }

    public List<ConditionLogResponse> getAllConditionLog() {
        return conditionLogRepository.findAll()
                .stream()
                .map(this::mapToConditionLogResponse)
                .toList();
    }

    public ConditionLogResponse getConditionLogById(Long id) {
        return conditionLogRepository.findById(id)
                .map(this::mapToConditionLogResponse)
                .orElseThrow(() -> new RuntimeException("ConditionLog not found with id: " + id));
    }
}