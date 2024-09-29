package com.khantech.gaming.tms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WalletRequestDto {
    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be a positive number")
    private Long userId;

    @NotNull(message = "Wallet Name is required")
    private String walletName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }
}
