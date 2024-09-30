package com.khantech.gaming.tms.service.impl;

import com.khantech.gaming.tms.exception.TransactionNotAwaitingApprovalException;
import com.khantech.gaming.tms.exception.TransactionNotFoundException;
import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import com.khantech.gaming.tms.repository.TransactionRepository;
import com.khantech.gaming.tms.service.TransactionOperation;
import com.khantech.gaming.tms.util.BusinessException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.khantech.gaming.tms.util.Constants.TRANSACTION_APPROVAL_SERVICE;
import static com.khantech.gaming.tms.util.LogMessages.*;

@Service
@Qualifier(TRANSACTION_APPROVAL_SERVICE)
public class TransactionApprovalService implements TransactionOperation<Long, Transaction> {
    private static final Logger log = LoggerFactory.getLogger(TransactionApprovalService.class);

    private final TransactionRepository transactionRepository;

    public TransactionApprovalService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Executes the approval of a transaction. This method is responsible for approving a transaction
     * that is in an {@link TransactionStatus#AWAITING_APPROVAL} state. It retrieves the transaction
     * by its ID, validates that it is eligible for approval, and then changes the status to
     * {@link TransactionStatus#PENDING} before saving the updated transaction to the repository.
     *
     * <p>The method performs the following steps:
     * <ul>
     *   <li>Retrieves the transaction by its ID from the repository.</li>
     *   <li>Throws an exception if the transaction is not found.</li>
     *   <li>Validates that the transaction is in the {@link TransactionStatus#AWAITING_APPROVAL} state.</li>
     *   <li>If the transaction is not in the awaiting approval status, throws an exception.</li>
     *   <li>Updates the transaction status to {@link TransactionStatus#PENDING}.</li>
     *   <li>Saves the updated transaction to the repository.</li>
     * </ul>
     *
     * @param transactionId The ID of the transaction to be approved.
     *
     * @return The approved {@link Transaction} with updated status.
     *
     * @throws TransactionNotFoundException if the transaction with the provided ID is not found.
     */
    @Override
    @Transactional
    public Transaction execute(Long transactionId) {
        log.info(TRANSACTION_APPROVE, transactionId);

        Transaction transaction = getTransactionById(transactionId);

        transaction.setStatus(TransactionStatus.PENDING);
        log.info(TRANSACTION_STATUS_CHANGED, transactionId);
        return transactionRepository.save(transaction);
    }

    private Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findByIdAndStatus(transactionId, TransactionStatus.AWAITING_APPROVAL)
                .orElseThrow(() -> {
                    log.error(TRANSACTION_NOT_FOUND_LOG, transactionId);
                    throw new TransactionNotFoundException(BusinessException.TransactionNotAwaitingApprovalException, transactionId);
                });
    }
}
