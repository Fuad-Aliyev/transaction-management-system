package com.khantech.gaming.tms.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ApiMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -5324402497879153511L;
    private ApiInfo info;

    public ApiMessage() {
    }

    public ApiInfo getInfo() {
        return this.info;
    }

    public void setInfo(ApiInfo info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiMessage that = (ApiMessage) o;
        return Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }

    @Override
    public String toString() {
        return "ApiMessage{" +
                "info=" + info +
                '}';
    }
}
