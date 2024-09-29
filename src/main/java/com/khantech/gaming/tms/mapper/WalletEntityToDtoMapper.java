package com.khantech.gaming.tms.mapper;

import com.khantech.gaming.tms.dto.WalletResponseDto;
import com.khantech.gaming.tms.model.Wallet;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class WalletEntityToDtoMapper implements BaseCollectionMapper<Wallet, WalletResponseDto> {
    @Override
    public List<WalletResponseDto> convertToList(Collection<Wallet> collection) {
        return BaseCollectionMapper.super.convertToList(collection);
    }

    @Override
    public WalletResponseDto convert(Wallet wallet) {
        WalletResponseDto dto = new WalletResponseDto();
        dto.setId(wallet.getId());
        dto.setUserId(wallet.getUser().getId());
        dto.setName(wallet.getName());
        dto.setBalance(wallet.getBalance());
        return dto;
    }

    @Override
    public boolean validate(Wallet data) {
        return BaseCollectionMapper.super.validate(data);
    }
}
