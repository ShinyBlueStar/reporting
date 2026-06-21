package com.sample.system.platform.commons.contracts.errors;

public class DomainException extends RuntimeException {
    private final String errorCode;

    public DomainException(String message) {
        super(message);
        this.errorCode = null;
    }

    public DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public DomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public boolean hasErrorCode() {
        return errorCode != null && !errorCode.trim().isEmpty();
    }
}
