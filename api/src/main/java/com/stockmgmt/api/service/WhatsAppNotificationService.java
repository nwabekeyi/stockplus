package com.stockmgmt.api.service;

public interface WhatsAppNotificationService {
    void sendLowStockAlert(String phoneNumber, String productName, int currentStock, int threshold);
    void sendDebtReminder(String phoneNumber, String customerName, java.math.BigDecimal outstanding);
    void sendDailySummary(String phoneNumber, java.math.BigDecimal sales, java.math.BigDecimal profit);
}
