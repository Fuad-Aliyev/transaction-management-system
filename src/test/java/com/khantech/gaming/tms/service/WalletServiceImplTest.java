package com.khantech.gaming.tms.service;

import com.khantech.gaming.tms.exception.DuplicateWalletException;
import com.khantech.gaming.tms.model.TransactionType;
import com.khantech.gaming.tms.model.User;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.repository.WalletRepository;
import com.khantech.gaming.tms.service.impl.WalletServiceImpl;
import com.khantech.gaming.tms.util.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class WalletServiceImplTest {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private WalletServiceImpl walletService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setWallets(new ArrayList<>());
    }

    @Test
    void createWallet_whenWalletIsUnique_shouldCreateWalletSuccessfully() {
        //given - precondition or setup
        when(userService.fetchUserById(anyLong())).thenReturn(user);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when - action or the behaviour that we are going to test
        Wallet result = walletService.createWallet(1L, "New Wallet");

        //then - verify the output
        assertNotNull(result);
        assertEquals("New Wallet", result.getName());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void createWallet_whenWalletNameIsDuplicate_shouldThrowDuplicateWalletException() {
        //given - precondition or setup
        Wallet existingWallet = new Wallet();
        existingWallet.setName("Existing Wallet");
        user.getWallets().add(existingWallet);

        when(userService.fetchUserById(anyLong())).thenReturn(user);

        //when - action or the behaviour that we are going to test
        DuplicateWalletException exception = assertThrows(DuplicateWalletException.class, () ->
                walletService.createWallet(1L, "Existing Wallet"));

        //then - verify the output
        assertEquals(BusinessException.DuplicateWalletException, exception.getBusinessError());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void getWalletsByUserId_shouldReturnUserWallets() {
        //given - precondition or setup
        Wallet wallet1 = new Wallet();
        wallet1.setName("Wallet 1");
        Wallet wallet2 = new Wallet();
        wallet2.setName("Wallet 2");

        when(walletRepository.findByUserId(anyLong())).thenReturn(List.of(wallet1, wallet2));

        //when - action or the behaviour that we are going to test
        List<Wallet> wallets = walletService.getWalletsByUserId(1L);

        //then - verify the output
        assertEquals(2, wallets.size());
        verify(walletRepository, times(1)).findByUserId(1L);
    }

    @Test
    void findWalletByIdWithLock_whenWalletExists_shouldReturnWallet() {
        //given - precondition or setup
        Wallet wallet = new Wallet();
        wallet.setId(1L);

        when(walletRepository.findByIdWithLock(1L)).thenReturn(wallet);

        //when - action or the behaviour that we are going to test
        Wallet result = walletService.findWalletByIdWithLock(1L);

        //then - verify the output
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(walletRepository, times(1)).findByIdWithLock(1L);
    }

    @Test
    void findWalletByIdWithLock_whenWalletDoesNotExist_shouldReturnNull() {
        //given - precondition or setup
        when(walletRepository.findByIdWithLock(1L)).thenReturn(null);

        //when - action or the behaviour that we are going to test
        Wallet result = walletService.findWalletByIdWithLock(1L);

        //then - verify the output
        assertNull(result);
        verify(walletRepository, times(1)).findByIdWithLock(1L);
    }

    @Test
    void updateWalletBalance_whenDebit_shouldDecreaseBalance() {
        //given - precondition or setup
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(5000));

        //when - action or the behaviour that we are going to test
        walletService.updateWalletBalance(wallet, BigDecimal.valueOf(2000), TransactionType.DEBIT);

        //then - verify the output
        assertEquals(BigDecimal.valueOf(3000), wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void updateWalletBalance_whenCredit_shouldIncreaseBalance() {
        //given - precondition or setup
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(5000));

        //when - action or the behaviour that we are going to test
        walletService.updateWalletBalance(wallet, BigDecimal.valueOf(2000), TransactionType.CREDIT);

        //then - verify the output
        assertEquals(BigDecimal.valueOf(7000), wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }
}
