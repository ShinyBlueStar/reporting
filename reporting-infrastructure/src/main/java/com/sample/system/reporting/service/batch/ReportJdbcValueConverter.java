package com.sample.system.reporting.service.batch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

public final class ReportJdbcValueConverter {

    private ReportJdbcValueConverter() {
    }

    public static Object convert(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return instant.toString();
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.toInstant(ZoneOffset.UTC).toString();
        }
        if (value instanceof LocalDate localDate) {
            return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toString();
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant().toString();
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toString();
        }
        if (value instanceof java.sql.Time time) {
            return time.toLocalTime().toString();
        }
        if (value instanceof byte[]) {
            return "";
        }
        if (value instanceof Clob clob) {
            return readClob(clob);
        }
        if (value instanceof Number || value instanceof String || value instanceof Boolean) {
            return value;
        }

        String className = value.getClass().getName();
        if (className.startsWith("oracle.")) {
            return convertOracleValue(value);
        }
        return String.valueOf(value);
    }

    public static Object toJdbcParameter(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return Timestamp.from(instant);
        }
        if (value instanceof LocalDateTime localDateTime) {
            return Timestamp.valueOf(localDateTime);
        }
        if (value instanceof LocalDate localDate) {
            return java.sql.Date.valueOf(localDate);
        }
        return value;
    }

    private static Object convertOracleValue(Object value) {
        Object javaTimeValue = invokeNoArg(value, "offsetDateTimeValue");
        if (javaTimeValue == null) {
            javaTimeValue = invokeNoArg(value, "zonedDateTimeValue");
        }
        if (javaTimeValue == null) {
            javaTimeValue = invokeNoArg(value, "localDateTimeValue");
        }
        if (javaTimeValue != null) {
            return javaTimeValue.toString();
        }

        try {
            var timestampValueMethod = value.getClass().getMethod("timestampValue");
            Object timestamp = timestampValueMethod.invoke(value);
            if (timestamp instanceof Timestamp sqlTimestamp) {
                return sqlTimestamp.toInstant().toString();
            }
        } catch (ReflectiveOperationException ignored) {
            // fall through to string conversion
        }
        return value.toString();
    }

    private static Object invokeNoArg(Object value, String methodName) {
        try {
            Method method = value.getClass().getMethod(methodName);
            return method.invoke(value);
        } catch (NoSuchMethodException | IllegalAccessException ignored) {
            return null;
        } catch (InvocationTargetException ex) {
            return null;
        }
    }

    private static String readClob(Clob clob) {
        try {
            long length = clob.length();
            if (length <= 0) {
                return "";
            }
            return clob.getSubString(1, (int) Math.min(length, Integer.MAX_VALUE));
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot read CLOB value", ex);
        }
    }
}
