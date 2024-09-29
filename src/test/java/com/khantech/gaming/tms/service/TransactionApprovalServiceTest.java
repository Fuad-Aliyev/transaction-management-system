package com.khantech.gaming.tms.service;

import com.khantech.gaming.tms.exception.TransactionNotFoundException;
import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import com.khantech.gaming.tms.repository.TransactionRepository;
import com.khantech.gaming.tms.service.impl.TransactionApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TransactionApprovalServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionApprovalService transactionApprovalService;

    private Transaction awaitingApprovalTransaction;

    @BeforeEach
    void setUp() {
        awaitingApprovalTransaction = new Transaction();
        awaitingApprovalTransaction.setId(1L);
        awaitingApprovalTransaction.setStatus(TransactionStatus.AWAITING_APPROVAL);
    }

    @Test
    void execute_whenTransactionFoundAndAwaitingApproval_shouldApproveTransaction() {
        //given - precondition or setup
        when(transactionRepository.findByIdAndStatus(anyLong(), eq(TransactionStatus.AWAITING_APPROVAL)))
                .thenReturn(Optional.of(awaitingApprovalTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when - action or the behaviour that we are going to test
        Transaction result = transactionApprovalService.execute(1L);

        //then - verify the output
        assertNotNull(result);
        assertEquals(TransactionStatus.PENDING, result.getStatus());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void execute_whenTransactionNotFound_shouldThrowTransactionNotFoundException() {
        //given - precondition or setup
        when(transactionRepository.findByIdAndStatus(anyLong(), eq(TransactionStatus.AWAITING_APPROVAL)))
                .thenReturn(Optional.empty());

        //when - action or the behaviour that we are going to test
        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () ->
                transactionApprovalService.execute(1L));

        //then - verify the output
        assertEquals("Transaction with ID 1 is not awaiting approval", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
