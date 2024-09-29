package com.khantech.gaming.tms.exception;

import com.khantech.gaming.tms.util.BusinessException;

public class TransactionNotAwaitingApprovalException extends BaseApiRuntimeException{
    public TransactionNotAwaitingApprovalException(BusinessException exception, Object... params) {
        super(exception.getMessage(params), exception.getCode(), exception.getReason(params));
    }
}
