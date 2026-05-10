package com.overduefree.common;

public final class ErrorCode {

    public static final int SUCCESS = 0;
    public static final int BAD_REQUEST = 40000;
    public static final int UNAUTHORIZED = 40001;
    public static final int FORBIDDEN = 40003;
    public static final int NOT_FOUND = 40004;
    public static final int SERVER_ERROR = 50000;

    private ErrorCode() {
    }
}
