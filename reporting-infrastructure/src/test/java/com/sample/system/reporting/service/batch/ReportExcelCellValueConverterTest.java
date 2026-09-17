package com.sample.system.reporting.service.batch;

import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportExcelCellValueConverterTest {

    @Test
    void convertsDatabaseValuesToExcelSafeStrings() {
        assertEquals("", ReportExcelCellValueConverter.asString(null));
        assertEquals("1000.50", ReportExcelCellValueConverter.asString(new java.math.BigDecimal("1000.50")));
        assertEquals("12:30:00", ReportExcelCellValueConverter.asString(LocalTime.of(12, 30)));
        assertEquals("validtext", ReportExcelCellValueConverter.asString("valid\u0000text"));
    }

    @Test
    void preservesSecondsAndFractionalPrecisionForJavaTimeValues() {
        assertEquals("12:30:00.123", ReportExcelCellValueConverter.asString(LocalTime.of(12, 30, 0, 123_000_000)));
        assertEquals(
                "2026-08-02T12:30:00",
                ReportExcelCellValueConverter.asString(LocalDateTime.of(2026, 8, 2, 12, 30)));
    }

    @Test
    void sanitizesSheetNameForExcelRules() {
        assertEquals("Report", ReportExcelCellValueConverter.sanitizeSheetName(null));
        assertEquals("Loan_Portfolio", ReportExcelCellValueConverter.sanitizeSheetName("Loan/Portfolio"));
    }

    @Test
    void convertsJdbcTimeToString() {
        assertEquals("08:15:30", ReportJdbcValueConverter.convert(Time.valueOf("08:15:30")));
    }
}
