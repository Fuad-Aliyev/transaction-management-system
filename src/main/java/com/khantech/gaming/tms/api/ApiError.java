package com.khantech.gaming.tms.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ApiError implements Serializable {
    @Serial
    private static final long serialVersionUID = 1162397376431006294L;
    private String code;
    private String reason;
    private String description;

    public ApiError() {
    }

    public String getCode() {
        return this.code;
    }

    public String getReason() {
        return this.reason;
    }

    public String getDescription() {
        return this.description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiError apiError = (ApiError) o;
        return Objects.equals(code, apiError.code) && Objects.equals(reason, apiError.reason) && Objects.equals(description, apiError.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, reason, description);
    }

    public String toString() {
        return "ApiError(code=" + this.getCode() + ", reason=" + this.getReason() + ", description=" + this.getDescription() + ")";
    }
}
