package com.invtr.equipmentservice.service;

import com.invtr.equipmentservice.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final EquipmentRepository equipmentRepository;
    private final EmailService emailService;
    private final AuthServiceClient authServiceClient;

    private static final Map<String, Long> THRESHOLDS = Map.of(
            "electrical", 3L,
            "furniture",  3L,
            "utility",    10L
    );

    public void checkStockAndNotify(String itemName, String itemType) {
        Long threshold = THRESHOLDS.get(itemType.toLowerCase());

        if (threshold == null) {
            log.info("No threshold defined for type '{}', skipping stock check.", itemType);
            return;
        }

        long availableStock = equipmentRepository.countAvailableByName(itemName);

        if (availableStock < threshold) {
            try {
                List<String> adminEmails = authServiceClient.getAdminEmails();
                emailService.sendLowStockAlert(itemName, itemType, availableStock, threshold, adminEmails);
                log.info("Low stock alert sent for '{}' ({}) — stock: {}, threshold: {}",
                        itemName, itemType, availableStock, threshold);
            } catch (IOException e) {
                log.error("Failed to send low stock alert for '{}': {}", itemName, e.getMessage());
            }
        }
    }
}