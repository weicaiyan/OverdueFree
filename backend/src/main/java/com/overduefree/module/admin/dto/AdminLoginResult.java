package com.overduefree.module.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminLoginResult {

    private String token;
    private String role;
    private String displayName;
    private LocalDateTime expiresAt;
}
