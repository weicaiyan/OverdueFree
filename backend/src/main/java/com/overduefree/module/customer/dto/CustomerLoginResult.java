package com.overduefree.module.customer.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerLoginResult {

    private String token;
    private LocalDateTime expiresAt;
}
