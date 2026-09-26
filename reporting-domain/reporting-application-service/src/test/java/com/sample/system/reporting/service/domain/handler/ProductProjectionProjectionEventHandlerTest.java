package com.sample.system.reporting.service.domain.handler;

import com.sample.system.reporting.service.application.event.handler.ProductProjectionEventHandler;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.ports.input.ProductReportProjectionService;
import com.sample.system.reporting.service.domain.model.entity.ProductReport;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductProjectionProjectionEventHandlerTest {

    @Test
    void handlesProductAggregateType() {
        ProductReportProjectionService projectionService = mock(ProductReportProjectionService.class);
        ProductProjectionEventHandler handler =
                new ProductProjectionEventHandler(projectionService, new ReportingProjectionMapper());

        EventEnvelope envelope = new EventEnvelope(
                "event-1",
                "UPSERT",
                "PRODUCT",
                "101",
                1L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "product-service",
                "corr-1",
                Map.of("productCode", 101, "productName", "Personal Loan")
        );

        assertTrue(handler.supports(envelope));
        handler.handle(envelope);

        verify(projectionService).sync(any(ProductReport.class));
    }

    @Test
    void handlesContractAggregateType() {
        ProductReportProjectionService projectionService = mock(ProductReportProjectionService.class);
        ProductProjectionEventHandler handler =
                new ProductProjectionEventHandler(projectionService, new ReportingProjectionMapper());

        EventEnvelope envelope = new EventEnvelope(
                "event-2",
                "UPSERT",
                "CONTRACT",
                "202",
                2L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "product-service",
                "corr-2",
                Map.of("contractCode", 202, "contractName", "Retail Contract", "status", "ACTIVE")
        );

        assertTrue(handler.supports(envelope));
        handler.handle(envelope);

        verify(projectionService).sync(any(ProductReport.class));
    }
}
