package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.Notification;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.dto.request.CreateNotificationRequest;
import com.stockmgmt.api.entity.dto.response.NotificationResponse;
import com.stockmgmt.api.entity.enumeration.NotificationStatus;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.NotificationRepository;
import com.stockmgmt.api.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final StoreRepository storeRepository;

    @PostMapping("/stores/{storeId}/notifications")
    public ResponseEntity<NotificationResponse> createNotification(@PathVariable UUID storeId, @Valid @RequestBody CreateNotificationRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        Notification notification = Notification.builder()
                .store(store)
                .title(request.getTitle())
                .message(request.getMessage())
                .channel(request.getChannel())
                .target(request.getTarget())
                .build();
        notificationRepository.save(notification);
        return ResponseEntity.ok(map(notification));
    }

    @GetMapping("/stores/{storeId}/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable UUID storeId) {
        return ResponseEntity.ok(notificationRepository.findByStore_IdOrderByCreatedAtDesc(storeId).stream().map(this::map).toList());
    }

    @PostMapping("/stores/{storeId}/notifications/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setStatus(NotificationStatus.READ);
        notificationRepository.save(notification);
        return ResponseEntity.ok(map(notification));
    }

    private NotificationResponse map(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .storeId(notification.getStore().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .channel(notification.getChannel())
                .status(notification.getStatus())
                .target(notification.getTarget())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
