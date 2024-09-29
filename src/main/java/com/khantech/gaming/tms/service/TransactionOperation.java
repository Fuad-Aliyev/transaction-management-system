package com.khantech.gaming.tms.service;

public interface TransactionOperation<T, R> {
    R execute(T request);
}
