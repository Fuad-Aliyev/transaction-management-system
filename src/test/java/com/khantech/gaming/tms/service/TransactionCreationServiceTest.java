package com.khantech.gaming.tms.service;

import com.khantech.gaming.tms.dto.TransactionRequestDto;
import com.khantech.gaming.tms.exception.WalletNotFoundException;
import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import com.khantech.gaming.tms.model.TransactionType;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.repository.TransactionRepository;
import com.khantech.gaming.tms.service.impl.TransactionCreationService;
import com.khantech.gaming.tms.validation.TransactionValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TransactionCreationServiceTest {
    @Mock
    private WalletService walletService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionValidationHandler transactionValidationHandler;

    @InjectMocks
    private TransactionCreationService transactionCreationService;

    private Wallet wallet;
    private TransactionRequestDto transactionRequestDto;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000));

        transactionRequestDto = new TransactionRequestDto();
        transactionRequestDto.setWalletId(1L);
        transactionRequestDto.setAmount(BigDecimal.valueOf(100));
        transactionRequestDto.setTransactionType(TransactionType.DEBIT);
    }

    @Test
    void execute_whenWalletIsFound_shouldCreateTransaction() {
        //given - precondition or setup
        when(walletService.findWalletByIdWithLock(anyLong())).thenReturn(wallet);

        doAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setStatus(TransactionStatus.APPROVED);
            return null;
        }).when(transactionValidationHandler).validate(any(Transaction.class));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);
            return transaction;
        });

        //when - action or the behaviour that we are going to test
        Transaction result = transactionCreationService.execute(transactionRequestDto);

        //then - verify the output
        assertNotNull(result);
        assertEquals(1L, result.getWallet().getId());
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        assertEquals(TransactionType.DEBIT, result.getTransactionType());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(transactionValidationHandler, times(1)).validate(any(Transaction.class));
    }

    @Test
    void execute_whenWalletIsNotFound_shouldThrowWalletNotFoundException() {
        //given - precondition or setup
        when(walletService.findWalletByIdWithLock(anyLong())).thenReturn(null);

        //when - action or the behaviour that we are going to test
        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () ->
                transactionCreationService.execute(transactionRequestDto));

        //then - verify the output
        assertEquals("Wallet with ID 1 not found", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionValidationHandler, never()).validate(any(Transaction.class));
    }

    @Test
    void execute_whenTransactionIsApproved_shouldUpdateWalletBalance() {
        //given - precondition or setup
        when(walletService.findWalletByIdWithLock(anyLong())).thenReturn(wallet);

        doAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setStatus(TransactionStatus.APPROVED);
            return null;
        }).when(transactionValidationHandler).validate(any(Transaction.class));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);
            transaction.setStatus(TransactionStatus.APPROVED);
            return transaction;
        });

        //when - action or the behaviour that we are going to test
        Transaction result = transactionCreationService.execute(transactionRequestDto);

        //then - verify the output
        assertNotNull(result);
        assertEquals(TransactionStatus.APPROVED, result.getStatus());

        verify(walletService, times(1)).updateWalletBalance(wallet, BigDecimal.valueOf(100), TransactionType.DEBIT);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void execute_whenValidationFails_shouldSetStatusToRejectedAndSaveTransaction() {
        // Given - precondition or setup
        when(walletService.findWalletByIdWithLock(anyLong())).thenReturn(wallet);

        doAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setStatus(TransactionStatus.REJECTED);
            return null;
        }).when(transactionValidationHandler).validate(any(Transaction.class));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);  // Simulate that the transaction is saved with an ID
            return transaction;
        });

        // When - action or the behaviour that we are going to test
        Transaction result = transactionCreationService.execute(transactionRequestDto);

        // Then - verify the output
        assertNotNull(result);
        assertEquals(TransactionStatus.REJECTED, result.getStatus());

        verify(transactionRepository, times(1)).save(any(Transaction.class));

        verify(walletService, never()).updateWalletBalance(any(Wallet.class), any(BigDecimal.class), any(TransactionType.class));
    }
}
