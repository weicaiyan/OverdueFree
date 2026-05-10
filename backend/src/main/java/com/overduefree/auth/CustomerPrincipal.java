package com.overduefree.auth;

import lombok.Getter;

@Getter
public class CustomerPrincipal {

    private final Long customerId;
    private final Long sessionId;

    public CustomerPrincipal(Long customerId, Long sessionId) {
        this.customerId = customerId;
        this.sessionId = sessionId;
    }
}
