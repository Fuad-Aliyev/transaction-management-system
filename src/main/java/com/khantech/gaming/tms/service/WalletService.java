package com.khantech.gaming.tms.service;

import com.khantech.gaming.tms.model.TransactionType;
import com.khantech.gaming.tms.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    Wallet createWallet(Long userId, String walletName);
    void save(Wallet wallet);
    List<Wallet> getWalletsByUserId(Long userId);
    Wallet findWalletByIdWithLock(Long walletId);
    void updateWalletBalance(Wallet wallet, BigDecimal amount, TransactionType transactionType);
}
