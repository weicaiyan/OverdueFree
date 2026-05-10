package com.overduefree.module.lead.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeadHistoryItem {

    private Long id;
    private String source;
    private String surname;
    private String region;
    private BigDecimal debtAmount;
    private String debtType;
    private String debtDescription;
    private String ageRange;
    private String jobStatus;
    private String creditStatus;
    private String monthlyIncomeRange;
    private String monthlyExpenseRange;
    private LocalDateTime createdAt;
}
