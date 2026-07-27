package com.example.BFF_Service.controller;

import com.example.BFF_Service.dto.DashboardResponse;
import com.example.BFF_Service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/bff")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/dashboard/{userId}")
    public Mono<DashboardResponse> getDashboardInfo(
            @PathVariable UUID userId) {
        return dashboardService.getDashboard(userId);
    }
}
