package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerLimitResponse {
    private int maxUsers;
    private long usedUsers;
    private long remainingWorkers;
    private boolean canAddWorker;
    private String planName;
}
