package com.khantech.gaming.tms.validation;

import com.khantech.gaming.tms.model.Transaction;

public interface TransactionValidationHandler {
    void setNext(TransactionValidationHandler next);
    void validate(Transaction transaction);
}
