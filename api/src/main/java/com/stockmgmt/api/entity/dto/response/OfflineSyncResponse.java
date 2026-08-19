package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OfflineSyncResponse {
    private int accepted;
    private int rejected;
    private List<OfflineMutationResult> results;
}
