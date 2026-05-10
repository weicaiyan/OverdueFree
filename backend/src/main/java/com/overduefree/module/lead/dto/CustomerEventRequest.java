package com.overduefree.module.lead.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
public class CustomerEventRequest {

    @NotBlank
    @Size(max = 50)
    private String eventType;

    @Size(max = 100)
    private String sourcePage;

    @Size(max = 50)
    private String refType;

    private Long refId;
    private Map<String, Object> metadata;
}
