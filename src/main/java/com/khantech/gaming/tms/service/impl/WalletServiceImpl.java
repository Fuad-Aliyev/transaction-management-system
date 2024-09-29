package com.khantech.gaming.tms.service.impl;

import com.khantech.gaming.tms.exception.DuplicateWalletException;
import com.khantech.gaming.tms.model.TransactionType;
import com.khantech.gaming.tms.model.User;
import com.khantech.gaming.tms.model.Wallet;
import com.khantech.gaming.tms.repository.WalletRepository;
import com.khantech.gaming.tms.service.UserService;
import com.khantech.gaming.tms.service.WalletService;
import com.khantech.gaming.tms.util.BusinessException;
import com.khantech.gaming.tms.validation.WalletValidationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.khantech.gaming.tms.util.LogMessages.*;

/**
 * WalletServiceImpl provides implementations for managing wallets, including creating, updating balances,
 * and saving wallet information.
 * This class interacts with the WalletRepository and UserRepository to perform database operations.
 */
@Service
public class WalletServiceImpl implements WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;
    private final UserService userService;

    public WalletServiceImpl(WalletRepository walletRepository, UserService userService) {
        this.walletRepository = walletRepository;
        this.userService = userService;
    }

    /**
     * Creates a new wallet for a specified user and initializes the balance to 1000.
     *
     * @param userId     the ID of the user for whom the wallet is being created.
     * @param walletName the name of the wallet.
     * @return the created Wallet object.
     */
    @Override
    public Wallet createWallet(Long userId, String walletName) {
        log.info(WALLET_CREATE_REQUEST, userId, walletName);
        WalletValidationHelper.validateWalletName(walletName);

        // Fetch user and validate existence
        User user = userService.fetchUserById(userId);

        // Check for duplicate wallet name
        checkDuplicateWallet(user, walletName);

        // Create and save wallet
        Wallet wallet = buildWallet(user, walletName);
        Wallet createdWallet = walletRepository.save(wallet);

        log.info(WALLET_CREATED_SUCCESS, createdWallet.getId());
        return createdWallet;
    }

    private void checkDuplicateWallet(User user, String walletName) {
        boolean duplicateExists = user.getWallets().stream()
                .anyMatch(wallet -> wallet.getName().equalsIgnoreCase(walletName));

        if (duplicateExists) {
            log.error(DUPLICATE_WALLET_NAME_ERROR, walletName, user.getId());
            throw new DuplicateWalletException(BusinessException.DuplicateWalletException, walletName, user.getId());
        }
    }

    private Wallet buildWallet(User user, String walletName) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setName(walletName);
        wallet.setBalance(BigDecimal.valueOf(1000));  // Set initial balance
        return wallet;
    }

    /**
     * Saves the wallet information to the database.
     *
     * @param wallet the wallet to be saved.
     */
    @Override
    public void save(Wallet wallet) {
        log.info(WALLET_SAVE_REQUEST, wallet.getId());
        walletRepository.save(wallet);
        log.info(WALLET_SAVE_SUCCESS, wallet.getId());
    }

    /**
     * Retrieves all wallets for a specified user.
     *
     * @param userId the ID of the user whose wallets are being fetched.
     * @return a list of wallets associated with the user.
     */
    @Override
    public List<Wallet> getWalletsByUserId(Long userId) {
        log.info(WALLET_FIND_BY_USER_ID, userId);
        List<Wallet> wallets = walletRepository.findByUserId(userId);
        log.info(WALLET_FIND_BY_USER_SUCCESS, wallets.size(), userId);
        return wallets;
    }

    /**
     * Finds a wallet by its ID and applies a database lock for concurrency control.
     *
     * @param walletId the ID of the wallet to be found.
     * @return the Wallet object if found, or null if not found.
     */
    @Override
    public Wallet findWalletByIdWithLock(Long walletId) {
        log.info(WALLET_FIND_BY_ID_LOCK, walletId);
        return walletRepository.findByIdWithLock(walletId);
    }

    /**
     * Updates the balance of a wallet based on the transaction type (debit or credit).
     *
     * @param wallet         the wallet whose balance is to be updated.
     * @param amount         the amount to update the balance by.
     * @param transactionType the type of transaction (DEBIT or CREDIT).
     */
    @Override
    public void updateWalletBalance(Wallet wallet, BigDecimal amount, TransactionType transactionType) {
        log.info(WALLET_UPDATE_BALANCE, wallet.getId(), amount, transactionType);
        if (transactionType == TransactionType.DEBIT) {
            decreaseBalance(wallet, amount);
        } else if (transactionType == TransactionType.CREDIT) {
            increaseBalance(wallet, amount);
        }
        save(wallet);
    }

    /**
     * Decreases the balance of the wallet by a specified amount.
     *
     * @param wallet the wallet whose balance is to be decreased.
     * @param amount the amount to decrease the balance by.
     */
    private void decreaseBalance(Wallet wallet, BigDecimal amount) {
        log.info(WALLET_DECREASE_BALANCE, wallet.getId(), amount);
        wallet.setBalance(wallet.getBalance().subtract(amount));
    }

    /**
     * Increases the balance of the wallet by a specified amount.
     *
     * @param wallet the wallet whose balance is to be increased.
     * @param amount the amount to increase the balance by.
     */
    private void increaseBalance(Wallet wallet, BigDecimal amount) {
        log.info(WALLET_INCREASE_BALANCE_REQUEST, wallet.getId(), amount);
        wallet.setBalance(wallet.getBalance().add(amount));
    }
}
