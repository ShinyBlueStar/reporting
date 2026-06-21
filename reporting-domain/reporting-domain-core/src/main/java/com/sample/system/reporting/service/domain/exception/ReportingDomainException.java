package com.sample.system.reporting.service.domain.exception;

import com.sample.system.platform.commons.contracts.errors.DomainException;
import com.sample.system.reporting.service.domain.model.validation.ValidationError;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReportingDomainException extends DomainException {

    private final List<ValidationError> validationErrors;

    public ReportingDomainException(String message) {
        super(message);
        this.validationErrors = List.of();
    }

    public ReportingDomainException(String errorCode, String message) {
        super(errorCode, message);
        this.validationErrors = List.of();
    }

    public ReportingDomainException(String errorCode, String message, List<ValidationError> validationErrors) {
        super(errorCode, message);
        this.validationErrors = validationErrors == null
                ? List.of()
                : Collections.unmodifiableList(validationErrors);
    }

    public static ReportingDomainException validationFailed(List<ValidationError> validationErrors) {
        List<ValidationError> errors = validationErrors == null ? List.of() : validationErrors;
        String message = errors.stream()
                .map(error -> error.field() + ": " + error.message())
                .collect(Collectors.joining("; "));
        String errorCode = errors.isEmpty()
                ? ErrorCode.INVALID_INPUT_PARAMETER.getCode()
                : errors.getFirst().code();
        return new ReportingDomainException(errorCode, message, errors);
    }
}
