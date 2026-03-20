package com.invtr.equipmentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromEmail;

    private static final int MAX_RETRIES = 3;

    public void sendLowStockAlert(String itemName, String itemType, long currentStock, long threshold, List<String> adminEmails) {
        if (adminEmails == null || adminEmails.isEmpty()) {
            log.warn("No admin emails provided, skipping low stock alert for '{}'.", itemName);
            return;
        }

        String subject = "⚠️ Low Stock Alert: " + itemName;
        String body = String.format(
                "Stock alert for item '%s' (type: %s).%n" +
                        "Current stock: %d%n" +
                        "Minimum threshold: %d%n%n" +
                        "Please restock as soon as possible.",
                itemName, itemType, currentStock, threshold
        );

        for (String adminEmail : adminEmails) {
            sendWithRetry(adminEmail, subject, body);
        }
    }

    private void sendWithRetry(String to, String subject, String body) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("Email sent to {} (attempt {})", to, attempt);
                return;
            } catch (MailException e) {
                log.warn("Failed to send email to {} on attempt {}: {}", to, attempt, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(500L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        log.error("Failed to send email to {} after {} attempts.", to, MAX_RETRIES);
    }
}