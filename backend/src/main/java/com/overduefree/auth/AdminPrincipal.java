package com.overduefree.auth;

import lombok.Getter;

@Getter
public class AdminPrincipal {

    private final Long adminId;
    private final Long sessionId;
    private final String username;
    private final String displayName;
    private final String role;

    public AdminPrincipal(Long adminId, Long sessionId, String username, String displayName, String role) {
        this.adminId = adminId;
        this.sessionId = sessionId;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
    }
}
