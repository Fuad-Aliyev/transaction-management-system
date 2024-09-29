package com.khantech.gaming.tms.exception;

import com.khantech.gaming.tms.util.BusinessException;

public class UserNotFoundException extends BaseApiRuntimeException {

    public UserNotFoundException(BusinessException exception, Object... params) {
        super(exception.getMessage(params), exception.getCode(), exception.getReason(params));
    }
}
