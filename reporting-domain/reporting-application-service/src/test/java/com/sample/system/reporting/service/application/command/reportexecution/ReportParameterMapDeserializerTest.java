package com.sample.system.reporting.service.application.command.reportexecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ReportParameterMapDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void deserializesIso8601DateParametersToInstant() throws Exception {
        ExecuteReportCommand command = objectMapper.readValue(
                """
                {
                  "reportDefinitionId": 25,
                  "requestedBy": "swagger-user",
                  "reportFormat": "XLSX",
                  "parameters": {
                    "fromDate": "2025-01-01T00:00:00Z",
                    "toDate": "2026-12-31T23:59:59Z"
                  }
                }
                """,
                ExecuteReportCommand.class);

        assertEquals(25L, command.getReportDefinitionId());
        assertInstanceOf(Instant.class, command.getParameters().get("fromDate"));
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), command.getParameters().get("fromDate"));
        assertEquals(Instant.parse("2026-12-31T23:59:59Z"), command.getParameters().get("toDate"));
    }

    @Test
    void keepsNonDateStringsAsText() throws Exception {
        ExecuteReportCommand command = objectMapper.readValue(
                """
                {
                  "reportDefinitionId": 25,
                  "parameters": {
                    "fileNumber": "12345"
                  }
                }
                """,
                ExecuteReportCommand.class);

        assertEquals(Map.of("fileNumber", "12345"), command.getParameters());
    }
}
