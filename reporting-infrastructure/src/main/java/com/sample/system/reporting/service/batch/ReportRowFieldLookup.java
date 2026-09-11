package com.sample.system.reporting.service.batch;

import java.util.Map;

public final class ReportRowFieldLookup {

    private ReportRowFieldLookup() {
    }

    public static Object value(Map<String, Object> row, String field) {
        if (row == null || field == null || field.isBlank()) {
            return null;
        }
        if (row.containsKey(field)) {
            return row.get(field);
        }
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (field.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
