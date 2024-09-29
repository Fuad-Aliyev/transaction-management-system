package com.khantech.gaming.tms.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface BaseCollectionMapper<F, T> extends BaseMapper<F, T> {
    default Set<T> convertToSet(Collection<F> collection) {
        return collection == null ? Collections.EMPTY_SET : (Set)collection.stream().filter(this::validate).map(this::convert).collect(Collectors.toSet());
    }

    default List<T> convertToList(Collection<F> collection) {
        return collection == null ? Collections.EMPTY_LIST : (List)collection.stream().filter(this::validate).map(this::convert).collect(Collectors.toList());
    }
}
