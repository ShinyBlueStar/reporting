package com.sample.system.reporting.service.application.utility;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventPayloadReaderTest {

    @Test
    void stringValueFallsBackThroughAlternateKeys() {
        Map<String, Object> payload = Map.of("alt", "b", "third", "c");
        assertEquals("b", EventPayloadReader.stringValue(payload, "main", "alt"));
        assertEquals("c", EventPayloadReader.stringValue(payload, "main", "x", "third"));
        assertNull(EventPayloadReader.stringValue(payload, "main", "x", "y", "z"));
    }

    @Test
    void numericAndBooleanValuesAreParsedFromTextOrNumbers() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", "42");
        payload.put("amount", "10.50");
        payload.put("count", 3);
        payload.put("flag", "true");
        payload.put("date", "2026-01-31");

        assertEquals(42L, EventPayloadReader.longValue(payload, "id"));
        assertEquals(42L, EventPayloadReader.longValue(payload, "missing", "id"));
        assertEquals(new BigDecimal("10.50"), EventPayloadReader.bigDecimalValue(payload, "amount"));
        assertEquals(3, EventPayloadReader.integerValue(payload, "count"));
        assertEquals(Boolean.TRUE, EventPayloadReader.booleanValue(payload, "flag"));
        assertEquals(LocalDate.of(2026, 1, 31), EventPayloadReader.localDateValue(payload, "date"));
    }

    @Test
    void missingKeysReturnNull() {
        Map<String, Object> payload = Map.of();
        assertNull(EventPayloadReader.longValue(payload, "a"));
        assertNull(EventPayloadReader.bigDecimalValue(payload, "a"));
        assertNull(EventPayloadReader.booleanValue(payload, "a"));
        assertNull(EventPayloadReader.localDateValue(payload, "a"));
    }

    @Test
    void malformedNumberFailsLoudly() {
        assertThrows(NumberFormatException.class,
                () -> EventPayloadReader.longValue(Map.of("id", "abc"), "id"));
    }
}
