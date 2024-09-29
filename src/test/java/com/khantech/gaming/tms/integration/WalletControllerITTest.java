package com.khantech.gaming.tms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khantech.gaming.tms.dto.WalletRequestDto;
import com.khantech.gaming.tms.model.User;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.repository.UserRepository;
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
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class WalletControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Fuad");
        testUser.setWallets(new ArrayList<>());
        userRepository.save(testUser);
    }

    @Test
    void createWallet_whenValidRequest_shouldReturnWallet() throws Exception {
        //given - precondition or setup
        WalletRequestDto walletRequestDto = new WalletRequestDto();
        walletRequestDto.setUserId(testUser.getId());
        walletRequestDto.setWalletName("Test Wallet");

        //then - verify the output
        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name").value("Test Wallet"))
                .andExpect(jsonPath("$.item.balance").value(1000));
    }

    @Test
    void createWallet_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        //given - precondition or setup
        WalletRequestDto walletRequestDto = new WalletRequestDto();
        walletRequestDto.setWalletName("Test Wallet");

        //then - verify the output
        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWallets_whenUserHasWallets_shouldReturnListOfWallets() throws Exception {
        //given - precondition or setup
        Wallet wallet1 = new Wallet();
        wallet1.setUser(testUser);
        wallet1.setName("Wallet 1");
        wallet1.setBalance(BigDecimal.valueOf(1000));
        walletRepository.save(wallet1);

        Wallet wallet2 = new Wallet();
        wallet2.setUser(testUser);
        wallet2.setName("Wallet 2");
        wallet2.setBalance(BigDecimal.valueOf(2000));
        walletRepository.save(wallet2);

        //then - verify the output
        mockMvc.perform(get("/api/v1/wallets/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].name").value("Wallet 1"))
                .andExpect(jsonPath("$.items[1].name").value("Wallet 2"));
    }

    @Test
    void getWallets_whenUserHasNoWallets_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}

