package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateNotificationRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String message;
    private NotificationChannel channel = NotificationChannel.DASHBOARD;
    private String target;
}
