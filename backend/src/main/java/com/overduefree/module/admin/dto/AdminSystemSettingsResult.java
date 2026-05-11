package com.overduefree.module.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminSystemSettingsResult {

    private Integer customerLoginExpireDays;
    private LocalDateTime updatedAt;
}
