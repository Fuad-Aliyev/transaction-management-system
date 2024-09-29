package com.khantech.gaming.tms.service;

import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import com.khantech.gaming.tms.model.TransactionType;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.repository.TransactionRepository;
import com.khantech.gaming.tms.service.impl.TransactionBatchProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TransactionBatchProcessorTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TransactionBatchProcessor transactionBatchProcessor;

    private Wallet wallet;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000));

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(200));
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setWallet(wallet);
        transaction.setStatus(TransactionStatus.PENDING);
    }

    @Test
    void execute_whenTransactionsAreProcessedSuccessfully_shouldProcessTransactions() {
        //given - precondition or setup
        when(transactionRepository.findTransactionsWithWalletsByStatus(TransactionStatus.PENDING))
                .thenReturn(Arrays.asList(transaction));
        when(walletService.findWalletByIdWithLock(anyLong())).thenReturn(wallet);

        //when - action or the behaviour that we are going to test
        assertDoesNotThrow(() -> transactionBatchProcessor.execute(null));

        //then - verify the output
        verify(walletService, times(1)).updateWalletBalance(any(Wallet.class), any(BigDecimal.class), any(TransactionType.class));
        verify(walletService, times(1)).save(wallet);
        verify(transactionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void execute_whenNoPendingTransactions_shouldNotProcessAnyTransactions() {
        //given - precondition or setup
        when(transactionRepository.findTransactionsWithWalletsByStatus(TransactionStatus.PENDING))
                .thenReturn(Collections.emptyList());

        //when - action or the behaviour that we are going to test
        assertDoesNotThrow(() -> transactionBatchProcessor.execute(null));

        //then - verify the output
        verify(walletService, never()).updateWalletBalance(any(Wallet.class), any(BigDecimal.class), any(TransactionType.class));
        verify(walletService, never()).save(wallet);
        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void execute_whenMultipleWalletsWithTransactions_shouldProcessAllTransactions() {
        //given - precondition or setup
        Wallet wallet2 = new Wallet();
        wallet2.setId(2L);
        wallet2.setBalance(new BigDecimal("2000"));

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAmount(new BigDecimal("300"));
        transaction2.setTransactionType(TransactionType.CREDIT);
        transaction2.setWallet(wallet);
        transaction2.setStatus(TransactionStatus.PENDING);

        Transaction transaction3 = new Transaction();
        transaction3.setId(3L);
        transaction3.setAmount(new BigDecimal("500"));
        transaction3.setTransactionType(TransactionType.DEBIT);
        transaction3.setWallet(wallet2);
        transaction3.setStatus(TransactionStatus.PENDING);

        List<Transaction> pendingTransactions = Arrays.asList(transaction, transaction2, transaction3);

        when(transactionRepository.findTransactionsWithWalletsByStatus(TransactionStatus.PENDING))
                .thenReturn(pendingTransactions);
        when(walletService.findWalletByIdWithLock(1L)).thenReturn(wallet);
        when(walletService.findWalletByIdWithLock(2L)).thenReturn(wallet2);

        //when - action or the behaviour that we are going to test
        transactionBatchProcessor.execute(null);

        //then - verify the output
        verify(walletService).updateWalletBalance(wallet, new BigDecimal("200"), TransactionType.DEBIT);
        verify(walletService).updateWalletBalance(wallet, new BigDecimal("300"), TransactionType.CREDIT);
        verify(walletService).updateWalletBalance(wallet2, new BigDecimal("500"), TransactionType.DEBIT);

        verify(walletService, times(1)).save(wallet);
        verify(walletService, times(1)).save(wallet2);

        verify(transactionRepository).saveAll(anyList());
        verify(walletService).save(wallet);
        verify(walletService).save(wallet2);
    }

    @Test
    void execute_whenWalletNotFound_shouldLogErrorAndSkipProcessing() {
        //given - precondition or setup
        when(transactionRepository.findTransactionsWithWalletsByStatus(TransactionStatus.PENDING))
                .thenReturn(Arrays.asList(transaction));
        when(walletService.findWalletByIdWithLock(anyLong())).thenReturn(null);

        //when - action or the behaviour that we are going to test
        assertDoesNotThrow(() -> transactionBatchProcessor.execute(null));

        //then - verify the output
        verify(walletService, never()).updateWalletBalance(any(Wallet.class), any(BigDecimal.class), any(TransactionType.class));
        verify(walletService, never()).save(wallet);
        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void execute_whenExceptionOccursDuringProcessing_shouldLogErrorAndContinue() {
        //given - precondition or setup
        when(transactionRepository.findTransactionsWithWalletsByStatus(TransactionStatus.PENDING))
                .thenReturn(Arrays.asList(transaction));

        when(walletService.findWalletByIdWithLock(anyLong())).thenThrow(new RuntimeException("Error"));

        //when - action or the behaviour that we are going to test
        assertDoesNotThrow(() -> transactionBatchProcessor.execute(null));

        //then - verify the output
        verify(walletService, times(1)).findWalletByIdWithLock(anyLong());
        verify(walletService, never()).updateWalletBalance(any(Wallet.class), any(BigDecimal.class), any(TransactionType.class));
        verify(walletService, never()).save(wallet);
        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void processSingleTransaction_shouldUpdateWalletBalanceAndApproveTransaction() {
        //given - precondition or setup
        List<Transaction> approvedTransactions = mock(List.class);

        //when - action or the behaviour that we are going to test
        transactionBatchProcessor.processSingleTransaction(transaction, wallet, approvedTransactions);

        //then - verify the output
        verify(walletService, times(1)).updateWalletBalance(any(Wallet.class), eq(transaction.getAmount()), eq(TransactionType.DEBIT));
        verify(approvedTransactions, times(1)).add(transaction);
        verify(transactionRepository, never()).saveAll(anyList());
    }
}
