package com.overduefree.module.health.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthResult {

    private String overallStatus;
    private String applicationStatus;
    private String databaseStatus;
    private String databaseProductName;
    private String message;
    private String timeZone;
    private LocalDateTime checkedAt;
}
