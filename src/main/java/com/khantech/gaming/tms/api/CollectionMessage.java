package com.khantech.gaming.tms.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class CollectionMessage<D extends Serializable> extends ApiMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 2363409183368614043L;
    private Collection<D> items;

    public CollectionMessage() {
    }

    public Collection<D> getItems() {
        return this.items;
    }

    public void setItems(Collection<D> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CollectionMessage<?> that = (CollectionMessage<?>) o;
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), items);
    }

    @Override
    public String toString() {
        return "CollectionMessage{" +
                "items=" + items +
                '}';
    }
}
