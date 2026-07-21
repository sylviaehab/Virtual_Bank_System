package com.example.Transaction_Service.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferExecutionRequest {

    @NotNull(message = "transactionId is required")
    private UUID transactionId;
}
