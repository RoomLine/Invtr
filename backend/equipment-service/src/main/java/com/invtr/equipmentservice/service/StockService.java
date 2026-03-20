package com.invtr.equipmentservice.service;

import com.invtr.equipmentservice.enums.EquipmentType;
import com.invtr.equipmentservice.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final EquipmentRepository equipmentRepository;
    private final EmailService emailService;
    private final AuthServiceClient authServiceClient;

    private static final Map<EquipmentType, Long> THRESHOLDS = Map.of(
            EquipmentType.ELECTRICAL, 3L,
            EquipmentType.FURNITURE,  3L,
            EquipmentType.UTILITY,    10L
    );

    public void checkStockAndNotify(String itemName, EquipmentType itemType) {
        if (itemType == null) {
            log.warn("itemType is null for item '{}', skipping stock check.", itemName);
            return;
        }

        Long threshold = THRESHOLDS.get(itemType);

        if (threshold == null) {
            log.info("No threshold defined for type '{}', skipping stock check.", itemType);
            return;
        }

        long availableStock = equipmentRepository.countAvailableByName(itemName);

        if (availableStock < threshold) {
            try {
                List<String> adminEmails = authServiceClient.getAdminEmails();
                emailService.sendLowStockAlert(itemName, itemType.name(), availableStock, threshold, adminEmails);
                log.info("Low stock alert sent for '{}' ({}) — stock: {}, threshold: {}",
                        itemName, itemType, availableStock, threshold);
            } catch (Exception e) {
                log.error("Failed to send low stock alert for '{}': {}", itemName, e.getMessage());
            }
        }
    }
}