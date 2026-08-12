package com.sample.system.reporting.service.application.utility;

import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public final class ReportInstantParser {

    private ReportInstantParser() {
    }

    public static Object parseTextValueOrKeep(String text) {
        if (text.isEmpty()) {
            return text;
        }
        try {
            return Instant.parse(text);
        } catch (DateTimeParseException ignored) {
            return text;
        }
    }

    public static Instant parseRequired(Object value) {
        return parseRequired(value, defaultErrorMessage());
    }

    public static Instant parseRequiredNullable(Object value, String fieldName) {
        if (value == null) {
            return null;
        }
        return parseRequired(value, fieldName + " must be an ISO-8601 instant like 2025-01-01T00:00:00Z");
    }

    private static Instant parseRequired(Object value, String errorMessage) {
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof CharSequence text) {
            String trimmed = text.toString().trim();
            if (trimmed.isEmpty()) {
                throw invalid(errorMessage);
            }
            try {
                return Instant.parse(trimmed);
            } catch (DateTimeParseException ignored) {
                throw invalid(errorMessage);
            }
        }
        throw invalid(errorMessage);
    }

    private static String defaultErrorMessage() {
        return "Date/time parameters must be ISO-8601 instant values like 2025-01-01T00:00:00Z";
    }

    private static ReportingDomainException invalid(String message) {
        return new ReportingDomainException(
                ErrorCode.REPORT_PARAMETER_INVALID.getCode(),
                message);
    }
}
