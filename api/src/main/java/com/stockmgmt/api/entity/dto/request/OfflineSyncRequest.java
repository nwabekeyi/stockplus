package com.stockmgmt.api.entity.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class OfflineSyncRequest {
    private List<OfflineMutationRequest> mutations;
}
