package com.khantech.gaming.tms.service;

import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionType;

import java.math.BigDecimal;

public interface TransactionService {
    Transaction createTransaction(Long walletId, BigDecimal amount, TransactionType transactionType);
    Transaction approveTransaction(Long transactionId);
    void processPendingTransactions();
}
