package com.overduefree.module.lead.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminLeadQuery {

    public static final String TYPE_LOGIN_ONLY = "LOGIN_ONLY";
    public static final String TYPE_SUBMITTED = "SUBMITTED";

    private String keyword;
    private String region;
    private String leadType;
    private String debtType;
    private BigDecimal minDebtAmount;
    private BigDecimal maxDebtAmount;
    private Boolean viewedWechatQr;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Long page = 1L;
    private Long pageSize = 10L;
}
