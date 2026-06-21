package com.sample.system.reporting.service.domain.exception;

import com.sample.system.reporting.service.domain.model.validation.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportingDomainExceptionTest {

    @Test
    void errorCodesAreUnique() {
        long distinct = Arrays.stream(ErrorCode.values()).map(ErrorCode::getCode).distinct().count();
        assertEquals(ErrorCode.values().length, distinct);
    }

    @Test
    void carriesErrorCodeAndMessage() {
        ReportingDomainException ex = new ReportingDomainException(ErrorCode.REPORT_EXECUTION_NOT_FOUND.getCode(), "missing");
        assertEquals("15", ex.getErrorCode());
        assertEquals("missing", ex.getMessage());
    }

    @Test
    void carriesValidationErrors() {
        ValidationError error = new ValidationError("fromDate", "REQUIRED", "fromDate is required");
        ReportingDomainException ex = new ReportingDomainException("2", "invalid", List.of(error));
        assertEquals(1, ex.getValidationErrors().size());
        assertEquals("fromDate", ex.getValidationErrors().get(0).field());
    }
}
