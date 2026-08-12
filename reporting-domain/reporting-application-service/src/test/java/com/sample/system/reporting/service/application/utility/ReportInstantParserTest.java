package com.sample.system.reporting.service.application.utility;

import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportInstantParserTest {

    @Test
    void parseTextValueOrKeep_parsesIso8601ToInstant() {
        Object value = ReportInstantParser.parseTextValueOrKeep("2025-01-01T00:00:00Z");
        assertInstanceOf(Instant.class, value);
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), value);
    }

    @Test
    void parseTextValueOrKeep_keepsNonDateText() {
        assertEquals("12345", ReportInstantParser.parseTextValueOrKeep("12345"));
    }

    @Test
    void parseRequired_parsesInstantValues() {
        assertEquals(
                Instant.parse("2026-12-31T23:59:59Z"),
                ReportInstantParser.parseRequired("2026-12-31T23:59:59Z"));
    }

    @Test
    void parseRequiredNullable_returnsNullForNullInput() {
        assertEquals(null, ReportInstantParser.parseRequiredNullable(null, "fromDate"));
    }

    @Test
    void parseRequired_throwsForInvalidText() {
        assertThrows(ReportingDomainException.class, () -> ReportInstantParser.parseRequired("not-a-date"));
    }
}
