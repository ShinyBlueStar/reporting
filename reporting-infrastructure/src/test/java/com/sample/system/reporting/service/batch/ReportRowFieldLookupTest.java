package com.sample.system.reporting.service.batch;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportRowFieldLookupTest {

    @Test
    void exactMatchWinsThenCaseInsensitiveFallback() {
        Map<String, Object> row = Map.of("ID", 1, "name", "n");
        assertEquals(1, ReportRowFieldLookup.value(row, "ID"));
        assertEquals(1, ReportRowFieldLookup.value(row, "id"));
        assertEquals("n", ReportRowFieldLookup.value(row, "NAME"));
    }

    @Test
    void missingBlankOrNullInputsReturnNull() {
        assertNull(ReportRowFieldLookup.value(Map.of("a", 1), "b"));
        assertNull(ReportRowFieldLookup.value(Map.of("a", 1), " "));
        assertNull(ReportRowFieldLookup.value(null, "a"));
        assertNull(ReportRowFieldLookup.value(Map.of("a", 1), null));
    }
}
