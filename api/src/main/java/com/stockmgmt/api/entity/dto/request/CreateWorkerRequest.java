package com.stockmgmt.api.entity.dto.request;

import com.stockmgmt.api.entity.enumeration.WorkerRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateWorkerRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private WorkerRole role;

    private List<String> permissions;
}
