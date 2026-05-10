package com.overduefree.module.caseitem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SuccessCaseResult {

    private Long id;
    private String displayName;
    private String maskedPhone;
    private String overduePlatforms;
    private BigDecimal overdueAmount;
    private String handlingPlan;
    private String avatarUrl;
    private String detailText;
    private Integer sortOrder;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
