package com.khantech.gaming.tms.exception;

public class BaseApiRuntimeException extends RuntimeException {
    private String code;
    private String reason;

    public BaseApiRuntimeException(String message, String code, String reason) {
        super(message);
        this.code = code;
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}
