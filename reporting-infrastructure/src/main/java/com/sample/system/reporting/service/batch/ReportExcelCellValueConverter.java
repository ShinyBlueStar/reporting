package com.sample.system.reporting.service.batch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;

public final class ReportExcelCellValueConverter {

    private ReportExcelCellValueConverter() {
    }

    public static String asString(Object value) {
        return sanitize(toPlainValue(value));
    }

    public static Object convert(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean || value instanceof Number || value instanceof Date) {
            return value;
        }
        return asString(value);
    }

    public static String sanitizeSheetName(String sheetName) {
        if (sheetName == null || sheetName.isBlank()) {
            return "Report";
        }
        String sanitized = sheetName
                .replace('\\', '_')
                .replace('/', '_')
                .replace('?', '_')
                .replace('*', '_')
                .replace('[', '_')
                .replace(']', '_')
                .replace(':', '_')
                .trim();
        if (sanitized.length() > 31) {
            sanitized = sanitized.substring(0, 31);
        }
        return sanitized.isBlank() ? "Report" : sanitized;
    }

    public static String sanitize(String value) {
        if (value == null || value.isEmpty()) {
            return value == null ? "" : value;
        }
        StringBuilder sanitized = new StringBuilder(value.length());
        for (int offset = 0; offset < value.length(); ) {
            int codePoint = value.codePointAt(offset);
            if (isValidXmlCharacter(codePoint)) {
                sanitized.appendCodePoint(codePoint);
            }
            offset += Character.charCount(codePoint);
        }
        return sanitized.toString();
    }

    private static String toPlainValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof CharSequence text) {
            return text.toString();
        }
        if (value instanceof byte[]) {
            return "";
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.toLocalDate() + "T" + formatLocalTime(dateTime.toLocalTime());
        }
        if (value instanceof LocalDate date) {
            return date.toString();
        }
        if (value instanceof LocalTime time) {
            return formatLocalTime(time);
        }
        if (value instanceof TemporalAccessor temporal) {
            return temporal.toString();
        }
        if (value instanceof Date date) {
            return date.toString();
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        if (value instanceof Double doubleValue) {
            if (doubleValue.isNaN() || doubleValue.isInfinite()) {
                return "";
            }
            return BigDecimal.valueOf(doubleValue).toPlainString();
        }
        if (value instanceof Float floatValue) {
            if (floatValue.isNaN() || floatValue.isInfinite()) {
                return "";
            }
            return BigDecimal.valueOf(floatValue).toPlainString();
        }
        if (value instanceof Map<?, ?> || value instanceof Iterable<?>) {
            return String.valueOf(value);
        }
        return String.valueOf(value);
    }

    private static String formatLocalTime(LocalTime time) {
        String value = time.toString();
        return value.length() == 5 ? value + ":00" : value;
    }

    private static boolean isValidXmlCharacter(int codePoint) {
        return codePoint == 0x9
                || codePoint == 0xA
                || codePoint == 0xD
                || (codePoint >= 0x20 && codePoint <= 0xD7FF)
                || (codePoint >= 0xE000 && codePoint <= 0xFFFD)
                || (codePoint >= 0x10000 && codePoint <= 0x10FFFF);
    }
}
