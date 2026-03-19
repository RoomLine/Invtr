package com.invtr.equipmentservice.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    public void sendLowStockAlert(String itemName, String itemType, long currentStock, long threshold, List<String> adminEmails) throws IOException {
        if (adminEmails == null || adminEmails.isEmpty()) {
            return;
        }

        Email from = new Email(fromEmail);
        String subject = "⚠️ Low Stock Alert: " + itemName;
        String body = String.format(
                "Stock alert for item '%s' (type: %s).%n" +
                        "Current stock: %d%n" +
                        "Minimum threshold: %d%n%n" +
                        "Please restock as soon as possible.",
                itemName, itemType, currentStock, threshold
        );

        for (String adminEmail : adminEmails) {
            Mail mail = new Mail(from, subject, new Email(adminEmail), new Content("text/plain", body));
            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            if (response.getStatusCode() >= 400) {
                throw new IOException("SendGrid error: " + response.getStatusCode() + " - " + response.getBody());
            }
        }
    }
}