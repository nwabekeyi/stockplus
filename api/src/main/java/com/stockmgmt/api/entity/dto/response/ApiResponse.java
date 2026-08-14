package com.stockmgmt.api.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private String message = "";
    @Builder.Default
    private List<String> error = Collections.emptyList();
    @Builder.Default
    private T data = null;
}
