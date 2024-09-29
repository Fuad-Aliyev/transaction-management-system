package com.khantech.gaming.tms.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class SingleMessage<D extends Serializable> extends ApiMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 4117647868955211833L;
    private D item;

    public SingleMessage() {
    }

    public D getItem() {
        return this.item;
    }

    public void setItem(D item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SingleMessage<?> that = (SingleMessage<?>) o;
        return Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), item);
    }

    @Override
    public String toString() {
        return "SingleMessage{" +
                "item=" + item +
                '}';
    }
}
