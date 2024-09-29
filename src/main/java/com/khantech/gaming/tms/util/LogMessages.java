package com.khantech.gaming.tms.util;

public final class LogMessages {

    private LogMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Transaction log messages
    public static final String TRANSACTION_CREATE = "Creating transaction for wallet ID: {}, amount: {}, transaction type: {}";
    public static final String TRANSACTION_APPROVED = "Transaction approved. Updating wallet balance for wallet ID: {}";
    public static final String TRANSACTION_SAVED = "Transaction successfully saved with ID: {}";
    public static final String TRANSACTION_PROCESSING_WALLET = "Processing {} transactions for wallet ID: {}";
    public static final String TRANSACTION_APPROVED_LIST = "Transaction ID: {} approved and added to the approved transactions list";
    public static final String TRANSACTION_APPROVE = "Approving transaction with ID: {}";
    public static final String TRANSACTION_PROCESS_ALL_PENDING = "Processing all pending transactions";
    public static final String TRANSACTION_PROCESS_SINGLE = "Processing transaction ID: {}, amount: {}, type: {}";

    //Transaction balance check log messages
    public static final String FOUND_PENDING_TRANSACTIONS = "Found {} pending transactions for wallet ID: {}. Calculating pending debit total...";
    public static final String PENDING_DEBIT_TOTAL = "Total pending debit for wallet ID: {} is {}";
    public static final String EFFECTIVE_BALANCE_CALCULATED = "Effective balance for wallet ID: {} is {}. Wallet current balance: {}";

    // Wallet log messages
    public static final String DUPLICATE_WALLET_NAME_ERROR = "Duplicate wallet name {} found for user ID {}";
    public static final String WALLET_NAME_EMPTY_ERROR = "Invalid wallet name: Name cannot be null or blank.";
    public static final String WALLET_NAME_INVALID_CHAR_ERROR = "Wallet name contains invalid characters.";
    public static final String WALLET_NOT_FOUND_FOR_TRANSACTIONS = "Wallet not found for transactions with Wallet ID: {}";
    public static final String WALLET_CREATE_REQUEST = "Creating a wallet for user ID: {}, wallet name: {}";
    public static final String WALLET_CREATED_SUCCESS = "Wallet created successfully with ID: {}";
    public static final String WALLET_SAVE_REQUEST = "Saving wallet with ID: {}";
    public static final String WALLET_SAVE_SUCCESS = "Wallet with ID: {} saved successfully";
    public static final String WALLET_FIND_BY_USER_ID = "Fetching wallets for user ID: {}";
    public static final String WALLET_FIND_BY_USER_SUCCESS = "Fetched {} wallets for user ID: {}";
    public static final String WALLET_FIND_BY_ID_LOCK = "Fetching wallet with ID: {} with lock";
    public static final String WALLET_INCREASE_BALANCE_REQUEST = "Increasing balance for wallet ID: {}, amount: {}";
    public static final String WALLET_DECREASE_BALANCE = "Decreasing balance for wallet ID: {}, amount: {}";
    public static final String WALLET_UPDATE_BALANCE = "Updating balance for wallet ID: {}, amount: {}, transaction type: {}";

    // Error log messages
    public static final String ERROR_PROCESSING_TRANSACTIONS = "Failed to process transactions for Wallet ID: {}";
    public static final String USER_NOT_FOUND_LOG = "User with ID {} not found.";
    public static final String TRANSACTION_NOT_FOUND_LOG = "Transaction with ID {} not found.";


    // Transaction Validation Messages
    public static final String START_NEGATIVE_AMOUNT_CHECK = "Starting validation: Checking if the transaction amount is negative. Amount: {}";
    public static final String NEGATIVE_AMOUNT_CHECK_PASSED = "Validation passed: The transaction amount is not negative. Amount: {}";
    public static final String NEGATIVE_AMOUNT_CHECK_FAILED = "Validation failed: Negative transaction amount. Amount: {}";
    public static final String START_INSUFFICIENT_BALANCE_CHECK = "Starting insufficient balance check for walletId: {}, transactionType: {}, balance: {}, transactionAmount: {}";
    public static final String INSUFFICIENT_BALANCE_CHECK_PASSED = "Insufficient balance check passed for walletId: {}, balance: {}, transactionAmount: {}";
    public static final String SKIPPING_BALANCE_VALIDATION_FOR_CREDIT = "Skipping balance validation for credit transaction. WalletId: {}, transactionType: {}, transactionAmount: {}";
    public static final String INSUFFICIENT_BALANCE_CHECK_FAILED = "Validation failed: Insufficient funds: Current balance is {}, but the transaction amount is {}.";
    public static final String START_TRANSACTION_THRESHOLD_CHECK = "Starting approval threshold validation for transaction with amount: {}";
    public static final String TRANSACTION_AMOUNT_EXCEEDS_THRESHOLD = "Transaction amount {} exceeds the approval threshold {}. Setting status to AWAITING_APPROVAL.";
    public static final String TRANSACTION_AMOUNT_WITHIN_THRESHOLD = "Transaction amount {} is within the approval threshold {}. Setting status to APPROVED.";
    public static final String START_ZERO_AMOUNT_CHECK = "Starting validation: Checking if the transaction amount is zero. Amount: {}";
    public static final String ZERO_AMOUNT_CHECK_PASSED = "Validation passed: The transaction amount is not zero. Amount: {}";
    public static final String ZERO_AMOUNT_CHECK_FAILED = "Validation failed: Zero transaction amount. Amount: {}";
    public static final String START_EFFECTIVE_BALANCE_CHECK = "Starting validation: Checking balance for wallet ID: {}, transaction type: {}, transaction amount: {}";
    public static final String EFFECTIVE_BALANCE_CHECK_FAILED = "Insufficient effective balance for wallet ID: {}. Required: {}, Available balance (with pending transactions): {}";

    // General
    public static final String TRANSACTION_STATUS_CHANGED = "Transaction status changed to PENDING for transaction ID: {}";
    public static final String TRANSACTION_NOT_AWAITING_APPROVAL = "Transaction with ID {} is not awaiting approval. Current status: {}";
    public static final String FETCHING_PENDING_TRANSACTIONS = "Fetching all pending transactions";
    public static final String GROUPING_PENDING_TRANSACTIONS = "Fetching and grouping all pending transactions by wallet ID";

    //Scheduler
    public static final String SCHEDULER_START_PROCESSING = "Scheduled task started: Processing pending transactions";
    public static final String SCHEDULER_END_PROCESSING = "Scheduled task completed: Finished processing pending transactions";

}
