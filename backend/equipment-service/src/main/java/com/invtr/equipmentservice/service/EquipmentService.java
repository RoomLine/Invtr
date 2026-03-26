package com.invtr.equipmentservice.service;

import com.invtr.equipmentservice.ai.GeminiService;
import com.invtr.equipmentservice.dto.*;
import com.invtr.equipmentservice.entity.ConditionLog;
import com.invtr.equipmentservice.entity.Equipment;
import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import com.invtr.equipmentservice.exception.ConditionLogNotFoundException;
import com.invtr.equipmentservice.exception.EquipmentNotFoundException;
import com.invtr.equipmentservice.repository.ConditionLogRepository;
import com.invtr.equipmentservice.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final ConditionLogRepository conditionLogRepository;
    private final StockService stockService;
    private final GeminiService geminiService;


    private EquipmentResponse mapToResponse(Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .serialNumber(equipment.getSerialNumber())
                .type(equipment.getType())
                .status(equipment.getStatus())
                .condition(equipment.getCondition())
                .qrCodeUrl(equipment.getQrCodeUrl())
                .location(equipment.getLocation())
                .isSensitive(equipment.getIsSensitive())
                .createdAt(equipment.getCreatedAt())
                .photoUrl(equipment.getPhotoUrl())
                .build();
    }

    private ConditionLogResponse mapToConditionLogResponse(ConditionLog conditionLog) {
        return ConditionLogResponse.builder()
                .id(conditionLog.getId())
                .equipmentId(conditionLog.getEquipmentId())
                .condition(conditionLog.getCondition())
                .loggedAt(conditionLog.getLoggedAt())
                .build();
    }

    public EquipmentResponse createEquipment(CreateEquipmentRequest request) {
        String generatedQrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="
                + URLEncoder.encode(request.getSerialNumber(), StandardCharsets.UTF_8);

        Equipment newEquipment = Equipment.builder()
                .name(request.getName())
                .type(request.getType())
                .serialNumber(request.getSerialNumber())
                .status(request.getStatus())
                .condition(request.getCondition())
                .location(request.getLocation())
                .qrCodeUrl(generatedQrUrl)
                .isSensitive(EquipmentType.ELECTRICAL.equals(request.getType()))
                .photoUrl(request.getPhotoUrl())
                .build();

        return mapToResponse(equipmentRepository.save(newEquipment));
    }

    public List<EquipmentResponse> getAllEquipment(EquipmentStatus status, EquipmentType type, EquipmentCondition condition) {
        return equipmentRepository.findWithFilters(status, type, condition)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EquipmentResponse getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EquipmentNotFoundException(id));
    }

    public void deleteEquipmentById(Long id) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));
        String name = existing.getName();
        EquipmentType type = existing.getType(); // fixed: was calling getEq() which doesn't exist
        equipmentRepository.deleteById(id);
        equipmentRepository.flush();
        stockService.checkStockAndNotify(name, type);
    }

    @Transactional
    public EquipmentResponse updateEquipmentById(UpdateEquipmentRequest updateRequest, Long id) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));

        if (updateRequest.getLocation() != null) {
            existing.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getCondition() != null) {
            existing.setCondition(updateRequest.getCondition());

            ConditionLog conditionLog = new ConditionLog();
            conditionLog.setEquipmentId(existing.getId());
            conditionLog.setCondition(updateRequest.getCondition());
            conditionLog.setLoggedAt(java.time.LocalDateTime.now());
            conditionLogRepository.save(conditionLog);
        }
        if (updateRequest.getType() != null) {
            existing.setType(updateRequest.getType());
            existing.setIsSensitive(EquipmentType.ELECTRICAL.equals(updateRequest.getType())); // fixed: was string compare
        }
        if (updateRequest.getStatus() != null) {
            existing.setStatus(updateRequest.getStatus());
        }

        Equipment saved = equipmentRepository.save(existing);

        if (EquipmentCondition.BROKEN.equals(saved.getCondition())) { // fixed: was string compare
            stockService.checkStockAndNotify(saved.getName(), saved.getType());
        }

        if (updateRequest.getStatus() != null) {
            EquipmentStatus newStatus = updateRequest.getStatus();
            boolean statusChangedToUnavailable =
                    EquipmentStatus.CHECKED_OUT.equals(newStatus) ||
                            EquipmentStatus.UNDER_REPAIR.equals(newStatus) ||
                            EquipmentStatus.RETIRED.equals(newStatus);

            if (statusChangedToUnavailable) {
                stockService.checkStockAndNotify(saved.getName(), saved.getType());
            }
        }

        return mapToResponse(saved);
    }

    @Transactional
    public EquipmentResponse updateEquipmentStatus(UpdateEquipmentStatusRequest updateStatusRequest, Long id) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));

        EquipmentStatus oldStatus = existing.getStatus();
        EquipmentStatus newStatus = updateStatusRequest.getStatus();

        existing.setStatus(newStatus);
        Equipment saved = equipmentRepository.save(existing);

        boolean statusChangedToUnavailable =
                (EquipmentStatus.CHECKED_OUT.equals(newStatus) ||
                        EquipmentStatus.UNDER_REPAIR.equals(newStatus) ||
                        EquipmentStatus.RETIRED.equals(newStatus)) &&
                        !newStatus.equals(oldStatus);

        if (statusChangedToUnavailable) {
            stockService.checkStockAndNotify(saved.getName(), saved.getType());
        }

        return mapToResponse(saved);
    }

    public List<ConditionLogResponse> getAllConditionLog(Long equipmentId, EquipmentCondition condition) {
        return conditionLogRepository.findWithFilters(equipmentId, condition)
                .stream()
                .map(this::mapToConditionLogResponse)
                .toList();
    }

    public ConditionLogResponse getConditionLogById(Long id) {
        return conditionLogRepository.findById(id)
                .map(this::mapToConditionLogResponse)
                .orElseThrow(() -> new ConditionLogNotFoundException(id));
    }

    public ConditionAssessmentResponse assessCondition(Long id, MultipartFile file) throws IOException {
        // verify equipment exists first
        equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));
        return geminiService.assessCondition(file);
    }
}