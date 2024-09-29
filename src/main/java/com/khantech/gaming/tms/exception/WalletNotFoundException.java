package com.khantech.gaming.tms.exception;

import com.khantech.gaming.tms.util.BusinessException;

public class WalletNotFoundException extends BaseApiRuntimeException {
    private final BusinessException businessError;

    public WalletNotFoundException(BusinessException exception, Object... params) {
        super(exception.getMessage(params), exception.getCode(), exception.getReason(params));
        this.businessError = exception;
    }

    public BusinessException getBusinessError() {
        return businessError;
    }
}
