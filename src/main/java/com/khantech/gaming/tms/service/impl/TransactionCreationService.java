package com.khantech.gaming.tms.service.impl;

import com.khantech.gaming.tms.dto.TransactionRequestDto;
import com.khantech.gaming.tms.exception.WalletNotFoundException;
import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import com.khantech.gaming.tms.model.TransactionType;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.repository.TransactionRepository;
import com.khantech.gaming.tms.service.TransactionOperation;
import com.khantech.gaming.tms.service.WalletService;
import com.khantech.gaming.tms.util.BusinessException;
import com.khantech.gaming.tms.util.LogMessages;
import com.khantech.gaming.tms.validation.TransactionValidationHandler;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.khantech.gaming.tms.util.Constants.TRANSACTION_CREATION_SERVICE;
import static com.khantech.gaming.tms.util.LogMessages.TRANSACTION_CREATE;
import static com.khantech.gaming.tms.util.LogMessages.TRANSACTION_SAVED;

@Service
@Qualifier(TRANSACTION_CREATION_SERVICE)
public class TransactionCreationService implements TransactionOperation<TransactionRequestDto, Transaction> {
    private static final Logger log = LoggerFactory.getLogger(TransactionCreationService.class);

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final TransactionValidationHandler transactionValidationHandler;

    public TransactionCreationService(WalletService walletService,
                                      TransactionValidationHandler transactionValidationHandler,
                                      TransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionValidationHandler = transactionValidationHandler;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Executes the creation of a new transaction. This method handles the end-to-end process of creating
     * a transaction, including wallet lookup, validation, balance checks, transaction approval, and saving
     * the transaction to the repository. The transaction type can be either DEBIT or CREDIT, and the method
     * checks whether the wallet has sufficient balance in case of a DEBIT transaction.
     *
     * <p>The method performs the following steps:
     * <ul>
     *   <li>Logs the start of the transaction creation process.</li>
     *   <li>Fetches the wallet by ID with a lock to ensure thread safety in concurrent scenarios.</li>
     *   <li>Initializes a new {@link Transaction} object with the provided details.</li>
     *   <li>Validates the transaction</li>
     *   <li>Processes transaction approval and updates the wallet balance accordingly.</li>
     *   <li>Saves the created transaction to the repository.</li>
     * </ul>
     *
     * @param request The {@link TransactionRequestDto} containing the transaction details, such as the wallet ID,
     *                transaction amount, and transaction type (DEBIT or CREDIT).
     *
     * @return The created and saved {@link Transaction}.
     *
     * @throws WalletNotFoundException if the wallet with the provided ID is not found.
     */
    @Override
    @Transactional
    public Transaction execute(TransactionRequestDto request) {
        log.info(TRANSACTION_CREATE, request.getWalletId(), request.getAmount(), request.getTransactionType());

        Wallet wallet = walletService.findWalletByIdWithLock(request.getWalletId());
        if (wallet == null) {
            throw new WalletNotFoundException(BusinessException.WalletNotFoundException, request.getWalletId());
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setTransactionType(request.getTransactionType());

        // Validate the transaction
        transactionValidationHandler.validate(transaction);

        // Process transaction approval and update balance
        processTransactionApproval(wallet, transaction, request.getAmount(), request.getTransactionType());

        // Save the transaction
        return saveTransaction(transaction);
    }

    private void processTransactionApproval(Wallet wallet, Transaction transaction,
                                            BigDecimal amount,
                                            TransactionType transactionType) {
        if (transaction.getStatus() == TransactionStatus.APPROVED) {
            log.info(LogMessages.TRANSACTION_APPROVED, wallet.getId());
            walletService.updateWalletBalance(wallet, amount, transactionType);
        }
    }

    private Transaction saveTransaction(Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info(TRANSACTION_SAVED, savedTransaction.getId());
        return savedTransaction;
    }
}