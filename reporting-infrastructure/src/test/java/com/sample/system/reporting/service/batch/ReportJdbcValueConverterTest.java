package com.sample.system.reporting.service.batch;

import oracle.sql.TIMESTAMPTZ;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReportJdbcValueConverterTest {

    @Test
    void convertsInstantToTimestampForJdbcBinding() {
        Instant instant = Instant.parse("2025-01-01T00:00:00Z");

        Object jdbcValue = ReportJdbcValueConverter.toJdbcParameter(instant);

        assertEquals(Timestamp.from(instant), jdbcValue);
    }

    @Test
    void convertsLocalDateTimeToTimestampForJdbcBinding() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 1, 12, 30, 0);

        Object jdbcValue = ReportJdbcValueConverter.toJdbcParameter(dateTime);

        assertEquals(Timestamp.valueOf(dateTime), jdbcValue);
    }

    @Test
    void convertsLocalDateToSqlDateForJdbcBinding() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        Object jdbcValue = ReportJdbcValueConverter.toJdbcParameter(date);

        assertEquals(java.sql.Date.valueOf(date), jdbcValue);
    }

    @Test
    void leavesOtherTypesUnchangedForJdbcBinding() {
        assertNull(ReportJdbcValueConverter.toJdbcParameter(null));
        assertEquals("ACTIVE", ReportJdbcValueConverter.toJdbcParameter("ACTIVE"));
    }

    @Test
    void convertsOracleTimestampWithTimeZoneToIsoString() throws SQLException {
        OffsetDateTime dateTime = OffsetDateTime.parse("2026-08-15T17:49:25+03:30");

        Object converted = ReportJdbcValueConverter.convert(new TIMESTAMPTZ(dateTime));

        assertEquals("2026-08-15T17:49:25+03:30", converted);
    }
}
