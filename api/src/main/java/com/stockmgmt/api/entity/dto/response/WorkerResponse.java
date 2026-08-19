package com.stockmgmt.api.entity.dto.response;

import com.stockmgmt.api.entity.enumeration.WorkerRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class WorkerResponse {
    private UUID id;
    private UUID storeId;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private WorkerRole role;
    private List<String> permissions;
    private boolean active;
    private LocalDateTime createdAt;
}
