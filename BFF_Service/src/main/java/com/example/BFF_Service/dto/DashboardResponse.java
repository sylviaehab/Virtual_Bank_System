package com.example.BFF_Service.dto;

import java.util.List;
import java.util.UUID;

public record DashboardResponse(
        UUID userId,
        String username,
        String email,
        String firstName,
        String lastName,
        List<AccountDashboardResponse> accounts) {
}
