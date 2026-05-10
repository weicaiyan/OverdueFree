package com.overduefree.common;

public final class ErrorCode {

    private ErrorCode() {}

    public static final int SUCCESS = 0;

    public static final int NOT_LOGGED_IN = 40001;
    public static final int TOKEN_EXPIRED = 40002;
    public static final int INVALID_PARAMS = 40003;
    public static final int NOT_FOUND = 40004;
    public static final int FORBIDDEN = 40005;
    public static final int DUPLICATE = 40006;

    public static final int INTERNAL_ERROR = 50000;
}
