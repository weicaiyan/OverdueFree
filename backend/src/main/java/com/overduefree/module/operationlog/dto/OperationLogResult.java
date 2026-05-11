package com.overduefree.module.operationlog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogResult {

    private Long id;
    private Long adminId;
    private String action;
    private String targetType;
    private Long targetId;
    private String detailJson;
    private LocalDateTime createdAt;
}
