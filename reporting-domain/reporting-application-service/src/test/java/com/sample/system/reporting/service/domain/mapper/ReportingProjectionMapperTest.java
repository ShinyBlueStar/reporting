package com.sample.system.reporting.service.domain.mapper;

import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.domain.model.entity.PartyReport;
import com.sample.system.reporting.service.domain.model.entity.ProductReport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportingProjectionMapperTest {

    private final ReportingProjectionMapper mapper = new ReportingProjectionMapper();

    @Test
    void mapsPartyReportFromEventEnvelope() {
        EventEnvelope envelope = new EventEnvelope(
                "event-1",
                "UPSERT",
                "PARTY",
                "party-1",
                2L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "party-service",
                "corr-1",
                Map.of(
                        "nationalCode", "1234567890",
                        "partyNo", "P001",
                        "partyName", "Ali",
                        "partyFamily", "Ahmadi",
                        "mobile", "09120000000",
                        "email", "ali@test.com"
                )
        );

        PartyReport report = mapper.eventEnvelopeToPartyReport(envelope);

        assertEquals("party-1", report.getAggregateId());
        assertEquals("event-1", report.getLastEventId());
        assertNotNull(report.getPayload());
        assertEquals("1234567890", report.getNationalCode());
        assertEquals("P001", report.getPartyNo());
        assertEquals("Ali", report.getPartyName());
    }

    @Test
    void mapsCreditFileIntoPartyProjection() {
        EventEnvelope envelope = new EventEnvelope(
                "event-3",
                "UPSERT",
                "CREDIT_FILE",
                "55",
                2L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "product-service",
                "corr-3",
                Map.of(
                        "fileNumber", "CF-100",
                        "nationalCode", "1234567890",
                        "creditLimit", "5000000",
                        "creditFileStatus", "ACTIVE",
                        "guarantors", Map.of("name", "Ali")
                )
        );

        PartyReport report = mapper.eventEnvelopeToPartyReport(envelope);

        assertEquals("55", report.getAggregateId());
        assertEquals("CF-100", report.getPartyNo());
        assertEquals("ACTIVE", report.getStatus());
        assertTrue(report.getPayload().contains("guarantors"));
    }

    @Test
    void mapsLoanMasterFieldsFromLoanEvent() {
        EventEnvelope envelope = new EventEnvelope(
                "event-2",
                "UPSERT",
                "LOAN",
                "1001",
                1L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "loan-service",
                "corr-2",
                Map.ofEntries(
                        Map.entry("requestedAmount", "1500000.50"),
                        Map.entry("installmentCount", "12"),
                        Map.entry("paidInstallmentCount", "3"),
                        Map.entry("loanFileNo", "LN-10001"),
                        Map.entry("fileNo", "5801207960"),
                        Map.entry("nationalCode", "0022201777"),
                        Map.entry("nationalName", "Ali Ahmadi"),
                        Map.entry("loanProfileName", "Personal Loan"),
                        Map.entry("unitCode", "60"),
                        Map.entry("unitName", "Central Branch"),
                        Map.entry("loanStatus", "ACTIVE"),
                        Map.entry("totalRemainingBeforeDueDate", "50000000"),
                        Map.entry("totalRemainingPastDueDate", "20000000"),
                        Map.entry("collaterals", Map.of("type", "House"))
                )
        );

        var report = mapper.eventEnvelopeToLoanReport(envelope);

        assertEquals(1001L, report.getSourceLoanId());
        assertEquals(new BigDecimal("1500000.50"), report.getRequestedAmount());
        assertEquals(12, report.getInstallmentCount());
        assertEquals(3, report.getPaidInstallmentCount());
        assertEquals("LN-10001", report.getLoanFileNo());
        assertEquals("5801207960", report.getFileNo());
        assertEquals("0022201777", report.getNationalCode());
        assertEquals("Personal Loan", report.getLoanProfileName());
        assertEquals("ACTIVE", report.getLoanStatus());
        assertTrue(report.getPayload().contains("collaterals"));
    }

    @Test
    void mapsProductAggregateIntoProductProjection() {
        EventEnvelope envelope = new EventEnvelope(
                "event-4",
                "UPSERT",
                "PRODUCT",
                "101",
                1L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "product-service",
                "corr-4",
                Map.of("productCode", 101, "productName", "Personal Loan", "active", true)
        );

        ProductReport report = mapper.eventEnvelopeToProductReport(envelope);

        assertEquals("101", report.getAggregateId());
        assertEquals(101L, report.getProductCode());
        assertEquals("Personal Loan", report.getProductName());
        assertEquals("ACTIVE", report.getStatus());
        assertNotNull(report.getPayload());
    }

    @Test
    void mapsContractAggregateIntoProductProjection() {
        EventEnvelope envelope = new EventEnvelope(
                "event-5",
                "UPSERT",
                "CONTRACT",
                "202",
                1L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "product-service",
                "corr-5",
                Map.of("contractCode", 202, "contractName", "Retail Contract", "status", "ACTIVE")
        );

        ProductReport report = mapper.eventEnvelopeToProductReport(envelope);

        assertEquals(202L, report.getProductCode());
        assertEquals("Retail Contract", report.getProductName());
        assertEquals("ACTIVE", report.getStatus());
    }
}
