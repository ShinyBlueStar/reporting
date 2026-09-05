package com.sample.system.reporting.service.application.reportexecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportParameterBindingServiceTest {

    private final ReportParameterBindingService service =
            new ReportParameterBindingService(new ObjectMapper().findAndRegisterModules());

    @Test
    void resolvesIso8601StringDateParameters() {
        ReportParameter fromDate = parameter("fromDate", "DATETIME");
        ReportParameter toDate = parameter("toDate", "DATETIME");

        Map<String, Object> resolved = service.resolveAndValidate(
                List.of(fromDate, toDate),
                Map.of(
                        "fromDate", "2025-01-01T00:00:00Z",
                        "toDate", "2026-12-31T23:59:59Z"));

        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), resolved.get("fromDate"));
        assertEquals(Instant.parse("2026-12-31T23:59:59Z"), resolved.get("toDate"));
    }

    @Test
    void rejectsInvalidDateParameter() {
        ReportParameter fromDate = parameter("fromDate", "DATETIME");

        ReportingDomainException exception = assertThrows(
                ReportingDomainException.class,
                () -> service.resolveAndValidate(
                        List.of(fromDate),
                        Map.of("fromDate", "01/01/2025")));

        assertEquals(
                "Date/time parameters must be ISO-8601 instant values like 2025-01-01T00:00:00Z",
                exception.getMessage());
    }

    private ReportParameter parameter(String name, String type) {
        ReportParameter parameter = new ReportParameter();
        parameter.setParameterName(name);
        parameter.setParameterType(type);
        parameter.setRequired(true);
        return parameter;
    }
}
