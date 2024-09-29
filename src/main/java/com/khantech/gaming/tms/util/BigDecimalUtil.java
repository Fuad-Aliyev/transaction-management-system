package com.khantech.gaming.tms.util;

import java.math.BigDecimal;

public final class BigDecimalUtil {
    private BigDecimalUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean lessThanSecond(BigDecimal firstValue, BigDecimal secondValue) {
        return firstValue.compareTo(secondValue) < 0;
    }

    public static boolean greaterThanSecond(BigDecimal firstValue, BigDecimal secondValue) {
        return firstValue.compareTo(secondValue) > 0;
    }

    public static boolean lessThanOrEqualToSecond(BigDecimal firstValue, BigDecimal secondValue) {
        return firstValue.compareTo(secondValue) <= 0;
    }

    public static boolean equalToSecond(BigDecimal firstValue, BigDecimal secondValue) {
        return firstValue.compareTo(secondValue) == 0;
    }
}
