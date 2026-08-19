package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.CreateWorkerRequest;
import com.stockmgmt.api.entity.dto.response.WorkerLimitResponse;
import com.stockmgmt.api.entity.dto.response.WorkerResponse;

import java.util.List;
import java.util.UUID;

public interface WorkerService {
    List<WorkerResponse> listWorkers(UUID storeId, User owner);
    WorkerLimitResponse getWorkerLimits(UUID storeId, User owner);
    WorkerResponse createWorker(UUID storeId, CreateWorkerRequest request, User owner);
}
