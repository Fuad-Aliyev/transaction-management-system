package com.khantech.gaming.tms.util;

public final class MessageFormatter {
    public static String formatMessage(String messageTemplate, Object... values) {
        return String.format(messageTemplate, values);
    }
}
