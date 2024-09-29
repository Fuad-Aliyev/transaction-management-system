package com.khantech.gaming.tms.validation;

import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.khantech.gaming.tms.util.BigDecimalUtil.lessThanSecond;
import static com.khantech.gaming.tms.util.Constants.TRANSACTION_NEGATIVE_AMOUNT;
import static com.khantech.gaming.tms.util.LogMessages.*;

@Component
public class NegativeAmountHandler implements TransactionValidationHandler {
    private static final Logger log = LoggerFactory.getLogger(NegativeAmountHandler.class);
    private TransactionValidationHandler nextHandler;

    @Override
    public void setNext(TransactionValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void validate(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        log.info(START_NEGATIVE_AMOUNT_CHECK, amount);
        if (lessThanSecond(transaction.getAmount(), BigDecimal.ZERO)) {
            log.error(NEGATIVE_AMOUNT_CHECK_FAILED, transaction.getAmount());
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setMessage(TRANSACTION_NEGATIVE_AMOUNT);
            return;
        }
        log.info(NEGATIVE_AMOUNT_CHECK_PASSED, amount);
        if (nextHandler != null) {
            nextHandler.validate(transaction);
        }
    }
}
