package com.khantech.gaming.tms.mapper;

import java.util.Optional;

public interface BaseMapper<F, T> extends BaseService {
    T convert(F var1);

    default Optional<T> convertSafely(F data) {
        return this.validate(data) ? Optional.of(this.convert(data)) : Optional.empty();
    }

    default boolean validate(F data) {
        return true;
    }
}
