package com.khantech.gaming.tms.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionValidationConfig {
    @Bean
    public TransactionValidationHandler transactionValidationHandler(
            InsufficientBalanceHandler insufficientBalanceHandler,
            NegativeAmountHandler negativeAmountHandler,
            ZeroAmountHandler zeroAmountHandler,
            ThresholdHandler thresholdHandler) {

        // Set up the chain of responsibility
        insufficientBalanceHandler.setNext(negativeAmountHandler);
        negativeAmountHandler.setNext(zeroAmountHandler);
        zeroAmountHandler.setNext(thresholdHandler);

        return insufficientBalanceHandler; // The first handler in the chain
    }
}
