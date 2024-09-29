package com.khantech.gaming.tms.validation;

import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.khantech.gaming.tms.util.BigDecimalUtil.greaterThanSecond;
import static com.khantech.gaming.tms.util.LogMessages.*;

@Component
public class ThresholdHandler implements TransactionValidationHandler {
    private static final Logger log = LoggerFactory.getLogger(NegativeAmountHandler.class);

    private TransactionValidationHandler nextHandler;
    @Value("${transaction.approval.threshold}")
    private BigDecimal approvalThreshold;

    @Override
    public void setNext(TransactionValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void validate(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        log.info(START_TRANSACTION_THRESHOLD_CHECK, amount);
        if (greaterThanSecond(amount, approvalThreshold)) {
            log.info(TRANSACTION_AMOUNT_EXCEEDS_THRESHOLD, amount, approvalThreshold);
            transaction.setStatus(TransactionStatus.AWAITING_APPROVAL);
        } else {
            log.info(TRANSACTION_AMOUNT_WITHIN_THRESHOLD, amount, approvalThreshold);
            transaction.setStatus(TransactionStatus.APPROVED);
        }
        if (nextHandler != null) {
            nextHandler.validate(transaction);
        }
    }
}
