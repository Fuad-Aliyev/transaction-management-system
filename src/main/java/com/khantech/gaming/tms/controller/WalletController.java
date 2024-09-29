package com.khantech.gaming.tms.controller;

import com.khantech.gaming.tms.api.ApiBuilder;
import com.khantech.gaming.tms.api.CollectionMessage;
import com.khantech.gaming.tms.api.SingleMessage;
import com.khantech.gaming.tms.dto.WalletRequestDto;
import com.khantech.gaming.tms.dto.WalletResponseDto;
import com.khantech.gaming.tms.mapper.WalletEntityToDtoMapper;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.service.impl.WalletServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@Tag(name = "Wallet", description = "Wallet Management API")
public class WalletController implements ApiBuilder {

    private final WalletServiceImpl walletServiceImpl;
    private final WalletEntityToDtoMapper walletMapper;

    public WalletController(WalletServiceImpl walletServiceImpl, WalletEntityToDtoMapper walletMapper) {
        this.walletServiceImpl = walletServiceImpl;
        this.walletMapper = walletMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new wallet",
            description = "Creates a new wallet for a user",
            tags = {"Wallet"})
    public ResponseEntity<SingleMessage<WalletResponseDto>> createWallet(
            @Valid @RequestBody WalletRequestDto walletRequestDto
    ) {
        Wallet wallet = walletServiceImpl.createWallet(walletRequestDto.getUserId(), walletRequestDto.getWalletName());
        return ResponseEntity.ok(generateSingleMessage(walletMapper.convert(wallet)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get all wallets for a user",
            description = "Fetches all wallets associated with a given user",
            tags = {"Wallet"})
    public ResponseEntity<CollectionMessage<WalletResponseDto>> getWallets(
            @Parameter(description = "ID of the user") @PathVariable Long userId
    ) {
        List<Wallet> wallets = walletServiceImpl.getWalletsByUserId(userId);
        return ResponseEntity.ok(generateCollectionMessage(walletMapper.convertToList(wallets)));
    }
}
