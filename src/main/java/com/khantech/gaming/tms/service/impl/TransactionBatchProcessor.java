package com.khantech.gaming.tms.service.impl;

import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.repository.TransactionRepository;
import com.khantech.gaming.tms.service.TransactionOperation;
import com.khantech.gaming.tms.service.WalletService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.khantech.gaming.tms.util.Constants.TRANSACTION_BATCH_PROCESSOR;
import static com.khantech.gaming.tms.util.LogMessages.*;

@Service
@Qualifier(TRANSACTION_BATCH_PROCESSOR)
public class TransactionBatchProcessor implements TransactionOperation<Void, Void> {
    private static final Logger log = LoggerFactory.getLogger(TransactionBatchProcessor.class);

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public TransactionBatchProcessor(TransactionRepository transactionRepository, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    /**
     * Executes the batch processing of pending transactions. The method retrieves all pending transactions,
     * groups them by wallet, and processes each wallet's transactions, updating wallet balances and approving transactions.
     *
     * <p>Logs are recorded at key points to provide visibility into the transaction processing flow.
     *
     * @param request The input parameter, which is {@link Void} since no input is needed.
     * @return The result, which is {@link Void} since no output is expected from this method.
     */
    @Override
    @Transactional
    public Void execute(Void request) {
        log.info(TRANSACTION_PROCESS_ALL_PENDING);
        // Group transactions by wallet and process each group
        Map<Long, List<Transaction>> transactionsByWallet = groupTransactionsByWallet();
        // List to collect transactions that are successfully approved during the processing
        // This helps in tracking which transactions were approved so that we can save or log them as necessary.
        List<Transaction> approvedTransactions = new ArrayList<>();

        transactionsByWallet.forEach((walletId, transactionsForWallet) ->
                processWalletTransactions(walletId, transactionsForWallet, approvedTransactions));
        if (!ObjectUtils.isEmpty(approvedTransactions)) {
            transactionRepository.saveAll(approvedTransactions);
        }
        return null;
    }

    /**
     * Processes all transactions for a specific wallet, updating the wallet's balance and approving transactions.
     * The method also ensures that any exceptions during processing are logged.
     *
     * @param walletId the ID of the wallet being processed.
     * @param transactionsForWallet the list of transactions associated with the wallet.
     * @param approvedTransactions the list to collect successfully processed transactions.
     */
    private void processWalletTransactions(Long walletId,
                                           List<Transaction> transactionsForWallet,
                                           List<Transaction> approvedTransactions) {
        try {
            Wallet wallet = walletService.findWalletByIdWithLock(walletId);
            if (wallet == null) {
                log.error(WALLET_NOT_FOUND_FOR_TRANSACTIONS, walletId);
                return;
            }
            log.info(TRANSACTION_PROCESSING_WALLET, transactionsForWallet.size(), walletId);
            transactionsForWallet.forEach(transaction -> processSingleTransaction(transaction, wallet, approvedTransactions));
            walletService.save(wallet);
        } catch (Exception e) {
            log.error(ERROR_PROCESSING_TRANSACTIONS, walletId, e);
        }
    }

    /**
     * Processes a single transaction for a wallet, updating the wallet's balance and marking the transaction as approved.
     *
     * @param transaction the transaction to process.
     * @param wallet the wallet associated with the transaction.
     * @param approvedTransactions the list to collect successfully processed transactions.
     */
    public void processSingleTransaction(Transaction transaction, Wallet wallet, List<Transaction> approvedTransactions) {
        log.info(TRANSACTION_PROCESS_SINGLE, transaction.getId(), transaction.getAmount(), transaction.getTransactionType());
        walletService.updateWalletBalance(wallet, transaction.getAmount(), transaction.getTransactionType());
        transaction.setStatus(TransactionStatus.APPROVED);
        approvedTransactions.add(transaction);
        log.info(TRANSACTION_APPROVED_LIST, transaction.getId());
    }

    private Map<Long, List<Transaction>> groupTransactionsByWallet() {
        log.info(GROUPING_PENDING_TRANSACTIONS);
        return getPendingTransactions().stream()
                .collect(Collectors.groupingBy(transaction -> transaction.getWallet().getId()));
    }

    private List<Transaction> getPendingTransactions() {
        log.info(FETCHING_PENDING_TRANSACTIONS);
        return transactionRepository.findTransactionsWithWalletsByStatus(TransactionStatus.PENDING);
    }
}
