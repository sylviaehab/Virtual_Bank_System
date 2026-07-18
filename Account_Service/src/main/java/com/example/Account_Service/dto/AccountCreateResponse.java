package com.example.Account_Service.dto;

import java.util.UUID;

public record AccountCreateResponse(
        UUID accountId,
        UUID accountNumber,
        String message
) {
}
