package com.overduefree.module.lead.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeadListItem {

    private String rowType;
    private Long customerId;
    private Long leadId;
    private String phone;
    private String surname;
    private String region;
    private BigDecimal debtAmount;
    private String debtType;
    private String source;
    private LocalDateTime firstLoginAt;
    private LocalDateTime leadCreatedAt;
    private Boolean viewedWechatQr;
    private LocalDateTime lastWechatQrViewAt;
    private String latestEventType;
    private LocalDateTime latestEventAt;
    private Long historyCount;
}
