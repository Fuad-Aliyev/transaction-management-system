package com.khantech.gaming.tms.mapper;

import com.khantech.gaming.tms.dto.TransactionResponseDto;
import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.util.DateTimeConverter;
import org.springframework.stereotype.Component;

@Component
public class TransactionEntityToDtoMapper implements BaseMapper<Transaction, TransactionResponseDto> {
    @Override
    public TransactionResponseDto convert(Transaction transaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(transaction.getId());
        dto.setWalletId(transaction.getWallet().getId());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(DateTimeConverter.convertDateToMillis(transaction.getCreatedAt()));
        dto.setMessage(transaction.getMessage());
        return dto;
    }

    @Override
    public boolean validate(Transaction data) {
        return data != null;
    }
}
