package com.khantech.gaming.tms.validation;

import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.khantech.gaming.tms.util.BigDecimalUtil.equalToSecond;
import static com.khantech.gaming.tms.util.Constants.TRANSACTION_ZERO_AMOUNT;
import static com.khantech.gaming.tms.util.LogMessages.*;

@Component
public class ZeroAmountHandler implements TransactionValidationHandler {
    private static final Logger log = LoggerFactory.getLogger(ZeroAmountHandler.class);
    private TransactionValidationHandler nextHandler;

    @Override
    public void setNext(TransactionValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void validate(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        log.info(START_ZERO_AMOUNT_CHECK, amount);
        if (equalToSecond(amount, BigDecimal.ZERO)) {
            log.error(ZERO_AMOUNT_CHECK_FAILED, amount);
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setMessage(TRANSACTION_ZERO_AMOUNT);
            return;
        }
        log.info(ZERO_AMOUNT_CHECK_PASSED, amount);
        if (nextHandler != null) {
            nextHandler.validate(transaction);
        }
    }
}
