package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhatsAppNotificationServiceImpl {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendLowStockAlert(String phoneNumber, String productName, int currentStock, int threshold) {
        sendWhatsAppMessage(phoneNumber,
                "Low Stock Alert: " + productName + " has " + currentStock + " units remaining (threshold: " + threshold + ")");
    }

    public void sendDebtReminder(String phoneNumber, String customerName, BigDecimal outstanding) {
        sendWhatsAppMessage(phoneNumber,
                "Dear " + customerName + ", this is a reminder that you have an outstanding balance of N" + outstanding + ". Please settle your debt. Thank you.");
    }

    public void sendDailySummary(String phoneNumber, BigDecimal sales, BigDecimal profit) {
        sendWhatsAppMessage(phoneNumber,
                "Daily Summary: Sales N" + sales + ", Profit N" + profit);
    }

    private void sendWhatsAppMessage(String phoneNumber, String message) {
        try {
            String url = "https://api.whatsapp.com/send";

            Map<String, String> body = new HashMap<>();
            body.put("phone", phoneNumber);
            body.put("message", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp message: " + e.getMessage());
        }
    }
}
