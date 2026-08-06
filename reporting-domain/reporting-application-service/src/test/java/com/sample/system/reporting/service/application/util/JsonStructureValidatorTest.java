package com.sample.system.reporting.service.application.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonStructureValidatorTest {

    private final JsonStructureValidator validator = new JsonStructureValidator(new ObjectMapper());

    @Test
    void acceptsValidJsonAndArrays() {
        assertDoesNotThrow(() -> validator.requireValidJson("{\"a\":1}"));
        assertDoesNotThrow(() -> validator.requireArray("[{\"field\":\"id\"}]"));
    }

    @Test
    void rejectsMalformedJson() {
        assertThrows(ReportingDomainException.class, () -> validator.requireValidJson("{not json"));
    }

    @Test
    void rejectsNonArrayWhenArrayRequired() {
        assertThrows(ReportingDomainException.class, () -> validator.requireArray("{\"a\":1}"));
    }
}
