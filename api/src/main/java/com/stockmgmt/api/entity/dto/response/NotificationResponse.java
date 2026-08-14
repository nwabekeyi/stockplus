package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.NotificationChannel;
import com.stockmgmt.api.entity.enumeration.NotificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private UUID storeId;
    private String title;
    private String message;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String target;
    private LocalDateTime createdAt;
}
