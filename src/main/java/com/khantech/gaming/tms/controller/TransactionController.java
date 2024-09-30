package com.khantech.gaming.tms.controller;

import com.khantech.gaming.tms.api.ApiBuilder;
import com.khantech.gaming.tms.api.SingleMessage;
import com.khantech.gaming.tms.dto.TransactionRequestDto;
import com.khantech.gaming.tms.dto.TransactionResponseDto;
import com.khantech.gaming.tms.mapper.TransactionEntityToDtoMapper;
import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.service.TransactionOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.khantech.gaming.tms.util.Constants.TRANSACTION_APPROVAL_SERVICE;
import static com.khantech.gaming.tms.util.Constants.TRANSACTION_CREATION_SERVICE;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction", description = "Transaction Management API")
public class TransactionController implements ApiBuilder {

    private final TransactionOperation<TransactionRequestDto, Transaction> transactionCreationService;
    private final TransactionOperation<Long, Transaction> transactionApprovalService;
    private final TransactionEntityToDtoMapper transactionMapper;

    public TransactionController(
            @Qualifier(TRANSACTION_CREATION_SERVICE) TransactionOperation<TransactionRequestDto, Transaction> transactionCreationService,
            @Qualifier(TRANSACTION_APPROVAL_SERVICE) TransactionOperation<Long, Transaction> transactionApprovalService,
            TransactionEntityToDtoMapper transactionMapper
    ) {
        this.transactionCreationService = transactionCreationService;
        this.transactionApprovalService = transactionApprovalService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new transaction",
            description = "Creates a new debit or credit transaction for the given wallet.",
            tags = {"Transaction"})
    public ResponseEntity<SingleMessage<TransactionResponseDto>> createTransaction(
            @Valid @RequestBody TransactionRequestDto transactionRequestDto
    ) {
        Transaction transaction = transactionCreationService.execute(transactionRequestDto);
        return ResponseEntity.ok(generateSingleMessage(transactionMapper.convert(transaction)));
    }

    @PostMapping("/{transactionId}/approve")
    @Operation(summary = "Approve a transaction",
            description = "Manually approve a pending transaction",
            tags = {"Transaction"})
    public ResponseEntity<SingleMessage<TransactionResponseDto>> approveTransaction(
            @Parameter(description = "ID of the transaction to be approved") @PathVariable Long transactionId
    ) {
        Transaction transaction = transactionApprovalService.execute(transactionId);
        return ResponseEntity.ok(generateSingleMessage(transactionMapper.convert(transaction)));
    }
}
