package com.sample.system.reporting.service.application.utility;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public final class EventPayloadReader {

    private EventPayloadReader() {
    }

    public static String stringValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : value.toString();
    }

    public static String stringValue(Map<String, Object> payload, String key, String alternateKey) {
        String value = stringValue(payload, key);
        return value != null ? value : stringValue(payload, alternateKey);
    }

    public static String stringValue(Map<String, Object> payload, String key, String alternateKey, String thirdKey) {
        String value = stringValue(payload, key, alternateKey);
        return value != null ? value : stringValue(payload, thirdKey);
    }

    public static String stringValue(Map<String, Object> payload, String key, String alternateKey,
                                     String thirdKey, String fourthKey) {
        String value = stringValue(payload, key, alternateKey, thirdKey);
        return value != null ? value : stringValue(payload, fourthKey);
    }

    public static Long longValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : Long.valueOf(value.toString());
    }

    public static Long longValue(Map<String, Object> payload, String key, String alternateKey) {
        Long value = longValue(payload, key);
        return value != null ? value : longValue(payload, alternateKey);
    }

    public static Boolean booleanValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(value.toString());
    }

    public static java.time.LocalDate localDateValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : java.time.LocalDate.parse(value.toString());
    }

    public static BigDecimal bigDecimalValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : new BigDecimal(value.toString());
    }

    public static Integer integerValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : Integer.valueOf(value.toString());
    }

    public static Instant instantValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : Instant.parse(value.toString());
    }
}
