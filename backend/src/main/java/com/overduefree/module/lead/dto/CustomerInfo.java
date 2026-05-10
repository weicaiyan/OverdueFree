package com.overduefree.module.lead.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerInfo {

    private Long customerId;
    private String phone;
    private String status;
    private LocalDateTime firstLoginAt;
    private LocalDateTime lastLoginAt;
}
