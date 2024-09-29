package com.khantech.gaming.tms.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class ApiInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 6423337072297179308L;
    private boolean success;
    private String status;
    private String timestamp;
    private Collection<ApiError> errors;

    public ApiInfo() {
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getStatus() {
        return this.status;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public Collection<ApiError> getErrors() {
        return this.errors;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setErrors(Collection<ApiError> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiInfo apiInfo = (ApiInfo) o;
        return success == apiInfo.success && Objects.equals(status, apiInfo.status) && Objects.equals(timestamp, apiInfo.timestamp) && Objects.equals(errors, apiInfo.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, status, timestamp, errors);
    }

    public String toString() {
        return "ApiInfo(success=" + this.isSuccess() + ", status=" + this.getStatus() + ", timestamp=" + this.getTimestamp() + ", errors=" + this.getErrors() + ")";
    }
}
