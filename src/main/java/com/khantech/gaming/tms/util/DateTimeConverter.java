package com.khantech.gaming.tms.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class DateTimeConverter {
    private DateTimeConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Long convertDateToMillis(LocalDateTime localDateTime) {
        if (localDateTime != null)
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return null;
    }
}
