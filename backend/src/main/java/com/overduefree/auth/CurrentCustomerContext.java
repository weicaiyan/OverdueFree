package com.overduefree.auth;

import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;

public final class CurrentCustomerContext {

    private static final ThreadLocal<CustomerPrincipal> CURRENT = new ThreadLocal<>();

    private CurrentCustomerContext() {
    }

    public static void set(CustomerPrincipal principal) {
        CURRENT.set(principal);
    }

    public static CustomerPrincipal getRequired() {
        CustomerPrincipal principal = CURRENT.get();
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return principal;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
