package com.khantech.gaming.tms.util;

public final class Constants {
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String TRANSACTION_CREATION_SERVICE = "transactionCreationService";
    public static final String TRANSACTION_APPROVAL_SERVICE = "transactionApprovalService";
    public static final String TRANSACTION_BATCH_PROCESSOR = "transactionBatchProcessor";

    //Transaction Message
    public static final String TRANSACTION_REJECTION_DUE_TO_INSUFFICIENT_BALANCE =
            "The wallet has insufficient funds. Current balance is %s, but the transaction amount is %s.";
    public static final String TRANSACTION_REJECTION_DUE_TO_INSUFFICIENT_EFFECTIVE_BALANCE =
            "The wallet has insufficient funds. Current balance is %s, pending debit transactions total %s, "
                    + "but the transaction amount is %s.";
    public static final String TRANSACTION_NEGATIVE_AMOUNT = "The amount can not be less than zero.";
    public static final String TRANSACTION_ZERO_AMOUNT = "The amount can not be zero.";
}
