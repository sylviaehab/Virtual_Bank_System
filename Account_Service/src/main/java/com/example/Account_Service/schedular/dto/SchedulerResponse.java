package com.example.Account_Service.schedular.dto;

import java.time.Instant;

public record SchedulerResponse(
        int updatedAccounts,
        Instant dateTime) {
}
