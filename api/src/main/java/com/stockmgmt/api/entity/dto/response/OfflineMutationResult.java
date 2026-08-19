package com.stockmgmt.api.entity.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfflineMutationResult {
    private String clientMutationId;
    private String status;
    private String error;
}
