package com.example.BFF_Service.dto;

import java.util.UUID;

public record DashboardRequest(
        UUID userId
) {
}
