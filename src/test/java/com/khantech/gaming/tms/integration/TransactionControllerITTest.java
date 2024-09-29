package com.khantech.gaming.tms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khantech.gaming.tms.dto.TransactionRequestDto;
import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import com.khantech.gaming.tms.model.TransactionType;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.repository.TransactionRepository;
import com.khantech.gaming.tms.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class TransactionControllerITTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Wallet wallet;
    private TransactionRequestDto transactionRequestDto;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        transactionRepository.deleteAll();

        wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setName("Test Wallet");
        wallet = walletRepository.save(wallet);

        transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setStatus(TransactionStatus.AWAITING_APPROVAL);
        transaction = transactionRepository.save(transaction);

        transactionRequestDto = new TransactionRequestDto();
        transactionRequestDto.setWalletId(wallet.getId());
        transactionRequestDto.setAmount(BigDecimal.valueOf(100));
        transactionRequestDto.setTransactionType(TransactionType.DEBIT);
    }

    @Test
    void createTransaction_whenValidRequest_shouldReturnTransaction() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.amount").value(100))
                .andExpect(jsonPath("$.item.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.message").value((Object) null));
    }

    @Test
    void approveTransaction_whenValidRequest_shouldApproveTransaction() throws Exception {
        mockMvc.perform(post("/api/v1/transactions/" + transaction.getId() + "/approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(transaction.getId()))
                .andExpect(jsonPath("$.item.amount").value(100))
                .andExpect(jsonPath("$.item.status").value("PENDING"));
    }

    @Test
    void createTransaction_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        transactionRequestDto.setWalletId(null);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveTransaction_whenInvalidTransactionId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/transactions/99/approve")  // Non-existent ID
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
