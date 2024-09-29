package com.khantech.gaming.tms.validation;

import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import com.khantech.gaming.tms.model.TransactionType;
import com.khantech.gaming.tms.repository.TransactionRepository;
import com.khantech.gaming.tms.util.MessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.khantech.gaming.tms.util.BigDecimalUtil.lessThanSecond;
import static com.khantech.gaming.tms.util.Constants.TRANSACTION_REJECTION_DUE_TO_INSUFFICIENT_BALANCE;
import static com.khantech.gaming.tms.util.Constants.TRANSACTION_REJECTION_DUE_TO_INSUFFICIENT_EFFECTIVE_BALANCE;
import static com.khantech.gaming.tms.util.LogMessages.*;

@Component
public class InsufficientBalanceHandler implements TransactionValidationHandler {
    private static final Logger log = LoggerFactory.getLogger(InsufficientBalanceHandler.class);

    private final TransactionRepository transactionRepository;
    private TransactionValidationHandler nextHandler;

    public InsufficientBalanceHandler(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void setNext(TransactionValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }


    /**
     * Validates the given {@link Transaction} by checking the wallet's balance and ensuring it can cover
     * the transaction amount. This method first skips balance validation for credit transactions and proceeds
     * to the next handler if applicable. For debit transactions, it checks whether the wallet's balance and
     * effective balance (considering debit transactions in PENDING and AWAITING_APPROVAL statuses)
     * are sufficient to approve the transaction.
     * If the balance or effective balance is insufficient, the transaction is rejected.
     *
     * <p>The validation process consists of the following steps:</p>
     * <ul>
     *     <li>If the transaction is of type {@link TransactionType#CREDIT}, the balance validation is skipped and
     *      passed to the next handler.</li>
     *     <li>If the transaction is of type {@link TransactionType#DEBIT}, the wallet's balance is checked to see
     *      if it's less than the transaction amount.</li>
     *     <li>If the balance is insufficient, the transaction is rejected, and an appropriate message is set.</li>
     *     <li>If the balance check passes, the method calculates the wallet's effective balance by subtracting pending
     *      debit transactions and checks if it covers the transaction amount.</li>
     *     <li>If the effective balance is insufficient, the transaction is rejected, and an appropriate message is set.</li>
     *     <li>If both balance checks pass, the transaction is passed to the next handler in the validation chain.</li>
     * </ul>
     *
     * @param transaction The {@link Transaction} to be validated.
     */
    @Override
    public void validate(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        Long walletId = transaction.getWallet().getId();
        BigDecimal balance = transaction.getWallet().getBalance();

        log.info(START_EFFECTIVE_BALANCE_CHECK, walletId, transaction.getTransactionType(), amount);

        if (isCreditTransaction(transaction)) {
            log.info(SKIPPING_BALANCE_VALIDATION_FOR_CREDIT, walletId, transaction.getTransactionType(), amount);
            passToNextHandler(transaction);
            return;
        }

        //handle insufficient balance check
        if (isInsufficientBalance(balance, amount)) {
            handleInsufficientBalance(transaction, balance, amount);
            return;
        }

        //handle insufficient effective balance check
        List<Transaction> pendingTransactions = fetchPendingTransactions(walletId);
        BigDecimal pendingDebitTotal = getPendingDebitTotal(walletId, pendingTransactions);
        BigDecimal effectiveBalance = calculateEffectiveBalance(pendingDebitTotal, balance, walletId);
        if (isInsufficientEffectiveBalance(effectiveBalance, amount)) {
            handleInsufficientEffectiveBalance(transaction, balance, amount, pendingDebitTotal, effectiveBalance);
            return;
        }

        log.info(INSUFFICIENT_BALANCE_CHECK_PASSED, walletId, balance, amount);
        passToNextHandler(transaction);
    }

    private boolean isCreditTransaction(Transaction transaction) {
        return TransactionType.CREDIT.equals(transaction.getTransactionType());
    }

    private boolean isInsufficientBalance(BigDecimal balance, BigDecimal amount) {
        return lessThanSecond(balance, amount);
    }

    private void handleInsufficientBalance(Transaction transaction, BigDecimal balance, BigDecimal amount) {
        log.error(INSUFFICIENT_BALANCE_CHECK_FAILED, balance, amount);
        transaction.setStatus(TransactionStatus.REJECTED);
        transaction.setMessage(
                MessageFormatter.formatMessage(TRANSACTION_REJECTION_DUE_TO_INSUFFICIENT_BALANCE, balance, amount)
        );
    }

    private List<Transaction> fetchPendingTransactions(Long walletId) {
        List<TransactionStatus> statuses = Arrays.asList(TransactionStatus.AWAITING_APPROVAL, TransactionStatus.PENDING);
        List<Transaction> pendingTransactions = transactionRepository.findTransactionsByWalletIdAndStatusesWithLock(walletId, statuses);
        log.info(FOUND_PENDING_TRANSACTIONS, pendingTransactions.size(), walletId);
        return pendingTransactions;
    }

    private BigDecimal getPendingDebitTotal(Long walletId, List<Transaction> pendingTransactions) {
        BigDecimal pendingDebitTotal = calculatePendingAmount(pendingTransactions, TransactionType.DEBIT);
        log.debug(PENDING_DEBIT_TOTAL, walletId, pendingDebitTotal);
        return pendingDebitTotal;
    }

    private BigDecimal calculateEffectiveBalance(BigDecimal pendingDebitTotal, BigDecimal balance, Long walletId) {
        BigDecimal effectiveBalance = balance.subtract(pendingDebitTotal);
        log.debug(EFFECTIVE_BALANCE_CALCULATED, walletId, effectiveBalance, balance);
        return effectiveBalance;
    }

    private BigDecimal calculatePendingAmount(List<Transaction> pendingTransactions, TransactionType transactionType) {
        return pendingTransactions.stream()
                .filter(transaction -> transaction.getTransactionType() == transactionType)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isInsufficientEffectiveBalance(BigDecimal effectiveBalance, BigDecimal amount) {
        return lessThanSecond(effectiveBalance, amount);
    }

    private void handleInsufficientEffectiveBalance(Transaction transaction,
                                                    BigDecimal balance,
                                                    BigDecimal amount,
                                                    BigDecimal pendingDebitTotal,
                                                    BigDecimal effectiveBalance) {
        log.error(EFFECTIVE_BALANCE_CHECK_FAILED, transaction.getWallet().getId(), amount, effectiveBalance);
        transaction.setStatus(TransactionStatus.REJECTED);
        transaction.setMessage(
                MessageFormatter.formatMessage(
                        TRANSACTION_REJECTION_DUE_TO_INSUFFICIENT_EFFECTIVE_BALANCE, balance, pendingDebitTotal, amount
                )
        );
    }

    private void passToNextHandler(Transaction transaction) {
        if (nextHandler != null) {
            nextHandler.validate(transaction);
        }
    }
}
