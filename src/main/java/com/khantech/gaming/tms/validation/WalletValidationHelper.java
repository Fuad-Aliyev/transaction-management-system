package com.khantech.gaming.tms.validation;

import com.khantech.gaming.tms.exception.InvalidWalletNameException;
import com.khantech.gaming.tms.util.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.khantech.gaming.tms.util.LogMessages.WALLET_NAME_EMPTY_ERROR;
import static com.khantech.gaming.tms.util.LogMessages.WALLET_NAME_INVALID_CHAR_ERROR;

public final class WalletValidationHelper {
    private static final Logger log = LoggerFactory.getLogger(WalletValidationHelper.class);

    private WalletValidationHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Validates the wallet name for null, blank, or invalid characters.
     *
     * @param walletName The name of the wallet to be validated.
     */
    public static void validateWalletName(String walletName) {
        if (walletName == null || walletName.isBlank()) {
            log.error(WALLET_NAME_EMPTY_ERROR);
            throw new InvalidWalletNameException(BusinessException.EmptyWalletNameException);
        }

        if (!walletName.matches("[a-zA-Z0-9_\\-\\s]+")) {
            log.error(WALLET_NAME_INVALID_CHAR_ERROR);
            throw new InvalidWalletNameException(BusinessException.InvalidWalletNameFormatException);
        }
    }
}
