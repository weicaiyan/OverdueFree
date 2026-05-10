package com.overduefree.module.lead.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventHistoryItem {

    private Long id;
    private String eventType;
    private String sourcePage;
    private String refType;
    private Long refId;
    private String metadataJson;
    private LocalDateTime createdAt;
}
