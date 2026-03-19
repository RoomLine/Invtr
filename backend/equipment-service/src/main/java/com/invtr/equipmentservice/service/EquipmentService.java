package com.invtr.equipmentservice.service;


import com.invtr.equipmentservice.dto.CreateEquipmentRequest;
import com.invtr.equipmentservice.dto.EquipmentResponse;
import com.invtr.equipmentservice.dto.UpdateEquipmentRequest;
import com.invtr.equipmentservice.entity.Equipment;
import com.invtr.equipmentservice.repository.ConditionLogRepository;
import com.invtr.equipmentservice.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final ConditionLogRepository conditionLogRepository;

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
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Equipment not found with id: " + id);
        }
    }
    public EquipmentResponse updateEquipmentById(UpdateEquipmentRequest updateRequest, long id) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

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

        return mapToResponse(equipmentRepository.save(existing));
    }
}
