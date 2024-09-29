package com.khantech.gaming.tms.dto;

import com.khantech.gaming.tms.model.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionRequestDto {
    @NotNull(message = "Wallet ID is required")
    @Min(value = 1, message = "Wallet ID must be a positive number")
    private Long walletId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
