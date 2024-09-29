package com.khantech.gaming.tms.util;

public enum BusinessException {
    InsufficientBalanceException("Insufficient balance in wallet.",
            "INSUFFICIENT_BALANCE",
            "The wallet has insufficient funds. Current balance is %s, but the transaction amount is %s."),
    InsufficientBalanceWithPendingTransactionsExceptionMessage("Insufficient balance in wallet.",
            "INSUFFICIENT_BALANCE",
            "The wallet has insufficient funds. Current balance is %s, pending debit transactions total %s, but the transaction amount is %s."),
    NegativeAmountException("Transaction amount cannot be negative",
            "NEGATIVE_AMOUNT",
            "The amount is less than zero."),
    ZeroAmountException("Transaction amount cannot be zero",
            "ZERO_AMOUNT",
            "The amount is equal to zero."),
    WalletNotFoundException("Wallet with ID %s not found",
            "WALLET_NOT_FOUND",
            "The wallet does not exist."),
    TransactionNotFoundException("Transaction with ID %s not found",
            "TRANSACTION_NOT_FOUND",
            "Transaction does not exist."),
    TransactionNotAwaitingApprovalException("Transaction with ID %s is not awaiting approval",
            "TRANSACTION_NOT_AWAITING_APPROVAL",
            "Transaction status has to be in AWAITING_APPROVAL."),
    UserNotFoundException("User with ID %s not found",
            "USER_NOT_FOUND",
            "The user does not exist."),
    EmptyWalletNameException("Empty wallet name provided.",
            "INVALID_WALLET_NAME_EMPTY",
            "Wallet name cannot be empty."),

    InvalidWalletNameFormatException("Invalid wallet name format.",
            "INVALID_WALLET_NAME_FORMAT",
            "Only letters, numbers, spaces, underscores, and dashes are allowed."),
    DuplicateWalletException("Wallet with name '%s' already exists for user with ID %s.",
            "DUPLICATE_WALLET_NAME",
            "Duplicate wallet name detected for the same user."),
    InvalidTransactionTypeException("Invalid transaction type: %s. Transaction type can be DEBIT or CREDIT only",
            "INVALID_TRANSACTION_TYPE",
            "The transaction type provided is invalid.");

    BusinessException(String message, String code, String reason) {
        this.message = message;
        this.code = code;
        this.reason = reason;
    }

    private final String message;
    private final String code;
    private final String reason;

    public String getMessage(Object... params) {
        return String.format(this.message, params);
    }

    public String getCode() {
        return this.code;
    }

    public String getReason(Object... params) {
        return String.format(this.reason, params);
    }
}
