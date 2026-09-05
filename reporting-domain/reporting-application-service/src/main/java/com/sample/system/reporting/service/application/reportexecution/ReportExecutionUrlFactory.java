package com.sample.system.reporting.service.application.reportexecution;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReportExecutionUrlFactory {

    private final String publicBaseUrl;

    public ReportExecutionUrlFactory(
            @Value("${reporting.api.public-base-url:http://localhost:8022}") String publicBaseUrl) {
        this.publicBaseUrl = trimTrailingSlash(publicBaseUrl);
    }

    public String downloadUrl(Long reportExecutionId) {
        return publicBaseUrl + "/api/v1/report-execution/" + reportExecutionId + "/download";
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:8022";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
