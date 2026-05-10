package com.overduefree.auth;

import com.overduefree.common.BusinessException;
import com.overduefree.common.ErrorCode;

public final class CurrentAdminContext {

    private static final ThreadLocal<AdminPrincipal> CURRENT = new ThreadLocal<>();

    private CurrentAdminContext() {
    }

    public static void set(AdminPrincipal principal) {
        CURRENT.set(principal);
    }

    public static AdminPrincipal getRequired() {
        AdminPrincipal principal = CURRENT.get();
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return principal;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
