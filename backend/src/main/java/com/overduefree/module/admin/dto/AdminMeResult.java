package com.overduefree.module.admin.dto;

import lombok.Data;

@Data
public class AdminMeResult {

    private Long adminId;
    private String username;
    private String displayName;
    private String role;
}
