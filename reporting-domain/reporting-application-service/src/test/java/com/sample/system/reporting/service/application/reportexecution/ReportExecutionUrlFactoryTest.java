package com.sample.system.reporting.service.application.reportexecution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportExecutionUrlFactoryTest {

    @Test
    void buildsDownloadUrlAndIgnoresTrailingSlash() {
        assertEquals("https://r.example/api/v1/report-execution/5/download",
                new ReportExecutionUrlFactory("https://r.example/").downloadUrl(5L));
    }

    @Test
    void fallsBackToLocalhostWhenNotConfigured() {
        assertEquals("http://localhost:8022/api/v1/report-execution/1/download",
                new ReportExecutionUrlFactory(" ").downloadUrl(1L));
    }
}
