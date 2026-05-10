package com.overduefree.module.health.controller;

import com.overduefree.common.ApiResponse;
import com.overduefree.module.health.dto.HealthResult;
import com.overduefree.module.health.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public ApiResponse<HealthResult> getHealth() {
        return ApiResponse.ok(healthService.getHealth());
    }
}
