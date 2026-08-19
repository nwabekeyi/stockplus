package com.stockmgmt.api.entity.dto.request;

import lombok.Data;

@Data
public class OfflineMutationRequest {
    private String clientMutationId;
    private String method;
    private String endpoint;
    private Object body;
}
