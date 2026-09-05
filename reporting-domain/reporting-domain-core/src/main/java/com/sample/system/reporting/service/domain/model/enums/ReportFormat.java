package com.sample.system.reporting.service.domain.model.enums;

public enum ReportFormat {
    JSON("json", "application/json"),
    CSV("csv", "text/csv"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String extension;
    private final String contentType;

    ReportFormat(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String extension() {
        return extension;
    }

    public String contentType() {
        return contentType;
    }

    public static ReportFormat from(String value) {
        if (value == null || value.isBlank()) {
            return JSON;
        }
        for (ReportFormat format : values()) {
            if (format.name().equalsIgnoreCase(value) || format.extension.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported report format: " + value);
    }
}
