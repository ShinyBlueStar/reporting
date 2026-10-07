package com.sample.system.reporting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.dataaccess.repository.AccountReportRepository;
import com.sample.system.reporting.service.dataaccess.repository.PartyReportRepository;
import com.sample.system.reporting.service.dataaccess.repository.ReportEventRepository;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.domain.model.enums.EventStatus;
import com.sample.system.reporting.service.messaging.processor.ReportingEventProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(classes = ReportingServiceApplication.class)
class ReportingRabbitToDbIntegrationTest {

    @Autowired
    private ReportingEventProcessor reportingEventProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportEventRepository reportEventRepository;

    @Autowired
    private AccountReportRepository accountReportRepository;

    @Autowired
    private PartyReportRepository partyReportRepository;

    @Test
    void processesEventEnvelope_thenPersistsReportEvent_thenUpsertsAccountReport() throws Exception {
        EventEnvelope envelope = new EventEnvelope(
                "event-acc-1",
                "UPSERT",
                "ACCOUNT",
                "account-1",
                1L,
                Instant.parse("2026-06-01T12:00:00Z"),
                "account-service",
                "corr-1",
                Map.of(
                        "nationalCode", "1234567890",
                        "accountNumber", "100200300",
                        "accountType", "CURRENT",
                        "balance", new BigDecimal("10.25")
                )
        );

        reportingEventProcessor.process(objectMapper.writeValueAsString(envelope));

        await().atMost(ofSeconds(10)).untilAsserted(() -> {
            var reportEvent = reportEventRepository.findByEventId(envelope.eventId()).orElseThrow();
            assertEquals(EventStatus.PROCESSED, reportEvent.getStatus());
            assertNotNull(reportEvent.getProcessedAt());
        });

        await().atMost(ofSeconds(10)).untilAsserted(() -> {
            var accountEntityOpt = accountReportRepository.findByAggregateId(envelope.aggregateId());
            assertTrue(accountEntityOpt.isPresent());
            var accountEntity = accountEntityOpt.orElseThrow();
            assertEquals("1234567890", accountEntity.getNationalCode());
            assertEquals("100200300", accountEntity.getAccountNumber());
            assertEquals("CURRENT", accountEntity.getAccountType());
            assertEquals(new BigDecimal("10.25"), accountEntity.getBalance());
        });
    }

    @Test
    void processesPartyServiceEnvelope_thenFillsPartyProjection() throws Exception {
        EventEnvelope envelope = new EventEnvelope(
                "event-party-1",
                "PartyUpdated",
                "PARTY",
                "101",
                1_785_584_800_000L,
                Instant.parse("2026-08-01T12:00:00Z"),
                "party-service",
                "corr-party-1",
                Map.of(
                        "nationalCode", "1234567890",
                        "partyNo", "101",
                        "partyName", "Sara",
                        "partyFamily", "Ahmadi",
                        "mobile", "09121234567",
                        "mobileNumber", "09121234567",
                        "email", "sara@example.com",
                        "status", "ACTIVE"
                )
        );

        reportingEventProcessor.process(objectMapper.writeValueAsString(envelope));

        await().atMost(ofSeconds(10)).untilAsserted(() -> {
            var reportEvent = reportEventRepository.findByEventId(envelope.eventId()).orElseThrow();
            assertEquals(EventStatus.PROCESSED, reportEvent.getStatus());
        });

        await().atMost(ofSeconds(10)).untilAsserted(() -> {
            var party = partyReportRepository.findByAggregateId(envelope.aggregateId()).orElseThrow();
            assertEquals("1234567890", party.getNationalCode());
            assertEquals("101", party.getPartyNo());
            assertEquals("Sara", party.getPartyName());
            assertEquals("Ahmadi", party.getPartyFamily());
            assertEquals("09121234567", party.getMobile());
            assertEquals("09121234567", party.getMobileNumber());
            assertEquals("sara@example.com", party.getEmail());
            assertEquals("ACTIVE", party.getStatus());
            assertEquals(envelope.eventId(), party.getLastEventId());
            assertEquals(envelope.aggregateVersion(), party.getAggregateVersion());
        });
    }
}
