package com.overduefree.module.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserResult {

    private Long id;
    private String username;
    private String displayName;
    private String role;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
