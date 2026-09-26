package com.sample.system.reporting.service.application.mapper;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.domain.model.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static com.sample.system.reporting.service.application.utility.EventPayloadReader.*;
import static com.sample.system.reporting.service.application.utility.ProjectionPayloadSerializer.toJson;

@Component
public class ReportingProjectionMapper {

    public PartyReport eventEnvelopeToPartyReport(EventEnvelope eventEnvelope) {
        PartyReport report = new PartyReport();
        applyEnvelope(report, eventEnvelope);
        report.setNationalCode(stringValue(eventEnvelope.payload(), "nationalCode"));
        report.setPartyNo(stringValue(eventEnvelope.payload(), "partyNo", "customerNo", "fileNumber"));
        report.setPartyName(stringValue(eventEnvelope.payload(), "partyName", "customerName"));
        report.setPartyFamily(stringValue(eventEnvelope.payload(), "partyFamily", "customerFamily"));
        report.setMobile(stringValue(eventEnvelope.payload(), "mobile", "mobileNumber"));
        report.setMobileNumber(report.getMobile());
        report.setEmail(stringValue(eventEnvelope.payload(), "email"));
        report.setStatus(stringValue(eventEnvelope.payload(), "creditFileStatus", "fileNumberStatus", "status"));
        return report;
    }

    public CardReport eventEnvelopeToCardReport(EventEnvelope eventEnvelope) {
        CardReport report = new CardReport();
        applyEnvelope(report, eventEnvelope);
        report.setNationalCode(stringValue(eventEnvelope.payload(), "nationalCode"));
        report.setCardNumber(stringValue(eventEnvelope.payload(), "cardNumber"));
        report.setAccountNumber(stringValue(eventEnvelope.payload(), "accountNumber"));
        report.setCardStatus(stringValue(eventEnvelope.payload(), "cardStatus"));
        report.setStatus(report.getCardStatus());
        return report;
    }

    public LoanReport eventEnvelopeToLoanReport(EventEnvelope eventEnvelope) {
        LoanReport report = new LoanReport();
        applyEnvelope(report, eventEnvelope);
        report.setSourceLoanId(firstAvailableLong(eventEnvelope.payload(), "loanId", "sourceLoanId"));
        if (report.getSourceLoanId() == null) {
            report.setSourceLoanId(parseLongSafely(eventEnvelope.aggregateId()));
        }
        report.setPartyId(longValue(eventEnvelope.payload(), "partyId"));
        report.setNationalCode(stringValue(eventEnvelope.payload(), "nationalCode"));
        report.setNationalName(stringValue(eventEnvelope.payload(), "nationalName", "partyName"));
        report.setFileNo(stringValue(eventEnvelope.payload(), "fileNo", "fileNumber", "creditFileNumber"));
        report.setLoanFileNo(stringValue(eventEnvelope.payload(), "loanFileNo", "loanFileNumber"));
        report.setLoanProfileName(stringValue(eventEnvelope.payload(), "loanProfileName", "productName"));
        report.setUnitCode(stringValue(eventEnvelope.payload(), "unitCode"));
        report.setUnitName(stringValue(eventEnvelope.payload(), "unitName"));
        report.setCreationDate(firstAvailableInstant(eventEnvelope.payload(), "creationDate"));
        report.setRequestedAmount(firstAvailableBigDecimal(eventEnvelope.payload(), "requestedAmount", "loanAmount"));
        report.setTotalInterest(bigDecimalValue(eventEnvelope.payload(), "totalInterest"));
        report.setInstallmentCount(integerValue(eventEnvelope.payload(), "installmentCount"));
        report.setPaidInstallmentCount(integerValue(eventEnvelope.payload(), "paidInstallmentCount"));
        report.setFirstInstallmentDate(firstAvailableInstant(eventEnvelope.payload(), "firstInstallmentDate"));
        report.setLastInstallmentDate(firstAvailableInstant(eventEnvelope.payload(), "lastInstallmentDate"));
        report.setLoanStatus(stringValue(eventEnvelope.payload(), "loanStatus", "status"));
        report.setTotalRemainingPrincipal(bigDecimalValue(eventEnvelope.payload(), "totalRemainingPrincipal"));
        report.setTotalRemainingInterest(bigDecimalValue(eventEnvelope.payload(), "totalRemainingInterest"));
        report.setTotalRemainingPenalty(bigDecimalValue(eventEnvelope.payload(), "totalRemainingPenalty"));
        report.setTotalRemainingPostMaturityInterest(
                bigDecimalValue(eventEnvelope.payload(), "totalRemainingPostMaturityInterest"));
        report.setTotalRemainingBeforeDueDate(bigDecimalValue(eventEnvelope.payload(), "totalRemainingBeforeDueDate"));
        report.setTotalRemainingPastDueDate(bigDecimalValue(eventEnvelope.payload(), "totalRemainingPastDueDate"));
        return report;
    }

    public ProductReport eventEnvelopeToProductReport(EventEnvelope eventEnvelope) {
        ProductReport report = new ProductReport();
        applyEnvelope(report, eventEnvelope);
        report.setProductCode(longValue(eventEnvelope.payload(), "productCode", "contractCode"));
        report.setProductName(stringValue(eventEnvelope.payload(), "productName", "contractName"));
        report.setNationalId(stringValue(eventEnvelope.payload(), "nationalId"));
        if ("PRODUCT".equalsIgnoreCase(eventEnvelope.aggregateType())) {
            report.setStatus(booleanValue(eventEnvelope.payload(), "active") == null
                    ? null
                    : Boolean.TRUE.equals(booleanValue(eventEnvelope.payload(), "active")) ? "ACTIVE" : "INACTIVE");
        } else {
            report.setStatus(stringValue(eventEnvelope.payload(), "status"));
        }
        return report;
    }

    public AccountReport eventEnvelopeToAccountReport(EventEnvelope eventEnvelope) {
        AccountReport report = new AccountReport();
        applyEnvelope(report, eventEnvelope);
        report.setNationalCode(stringValue(eventEnvelope.payload(), "nationalCode"));
        report.setAccountNumber(stringValue(eventEnvelope.payload(), "accountNumber"));
        report.setAccountType(stringValue(eventEnvelope.payload(), "accountType"));
        report.setBalance(bigDecimalValue(eventEnvelope.payload(), "balance"));
        report.setStatus(stringValue(eventEnvelope.payload(), "accountStatus", "status"));
        return report;
    }

    public InstallmentReport eventEnvelopeToInstallmentReport(EventEnvelope eventEnvelope) {
        InstallmentReport report = new InstallmentReport();
        applyEnvelope(report, eventEnvelope);
        report.setSourceInstallmentId(firstAvailableLong(eventEnvelope.payload(), "installmentId", "sourceInstallmentId"));
        if (report.getSourceInstallmentId() == null) {
            report.setSourceInstallmentId(parseLongSafely(eventEnvelope.aggregateId()));
        }
        Long loanId = firstAvailableLong(eventEnvelope.payload(), "loanId", "loanAggregateId", "parentAggregateId");
        report.setLoanAggregateId(loanId == null ? null : loanId.toString());
        if (report.getLoanAggregateId() == null && eventEnvelope.payload() != null) {
            Object parentId = eventEnvelope.payload().get("loan");
            if (parentId instanceof Map<?, ?> loanPayload) {
                Object aggregateId = loanPayload.get("aggregateId");
                if (aggregateId != null) {
                    report.setLoanAggregateId(aggregateId.toString());
                }
            }
        }
        report.setLoanFileNo(stringValue(eventEnvelope.payload(), "loanFileNo", "loanFileNumber"));
        report.setInstallmentNo(firstAvailableInteger(eventEnvelope.payload(), "installmentNo", "installmentNumber"));
        report.setDueDate(firstAvailableInstant(eventEnvelope.payload(), "dueDate"));
        report.setInstallmentAmount(firstAvailableBigDecimal(eventEnvelope.payload(), "installmentAmount", "amount"));
        report.setTotalPaid(firstAvailableBigDecimal(eventEnvelope.payload(), "totalPaid", "paidAmount"));
        report.setInstallmentStatus(stringValue(eventEnvelope.payload(), "installmentStatus", "status"));
        report.setLastPaymentDate(firstAvailableInstant(eventEnvelope.payload(), "lastPaymentDate", "paidDate"));
        report.setRemainingTotal(bigDecimalValue(eventEnvelope.payload(), "remainingTotal"));
        report.setRemainingPrincipal(bigDecimalValue(eventEnvelope.payload(), "remainingPrincipal"));
        report.setRemainingInterest(bigDecimalValue(eventEnvelope.payload(), "remainingInterest"));
        report.setRemainingPenalty(bigDecimalValue(eventEnvelope.payload(), "remainingPenalty"));
        report.setRemainingPostMaturityInterest(
                bigDecimalValue(eventEnvelope.payload(), "remainingPostMaturityInterest"));
        return report;
    }

    public TransactionReport eventEnvelopeToTransactionReport(EventEnvelope eventEnvelope) {
        TransactionReport report = new TransactionReport();
        applyEnvelope(report, eventEnvelope);
        report.setAccountNumber(stringValue(eventEnvelope.payload(), "accountNumber"));
        report.setTransactionRef(stringValue(eventEnvelope.payload(), "transactionRef"));
        report.setTransactionType(stringValue(eventEnvelope.payload(), "transactionType"));
        report.setAmount(bigDecimalValue(eventEnvelope.payload(), "amount"));
        report.setTransactionDate(instantValue(eventEnvelope.payload(), "transactionDate"));
        report.setStatus(stringValue(eventEnvelope.payload(), "transactionStatus", "status"));
        return report;
    }

    private Instant instantValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return instant;
        }
        return Instant.parse(value.toString());
    }

    private Instant firstAvailableInstant(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Instant value = instantValue(payload, key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Integer firstAvailableInteger(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Integer value = integerValue(payload, key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private BigDecimal firstAvailableBigDecimal(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            BigDecimal value = bigDecimalValue(payload, key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Long firstAvailableLong(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Long value = parseLongSafely(stringValue(payload, key));
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Long parseLongSafely(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void applyEnvelope(ProjectionReport report, EventEnvelope eventEnvelope) {
        report.setAggregateId(eventEnvelope.aggregateId());
        report.setAggregateVersion(eventEnvelope.aggregateVersion());
        report.setLastEventId(eventEnvelope.eventId());
        report.setSourceService(eventEnvelope.source());
        report.setPayload(toJson(eventEnvelope.payload()));
        report.setReportDate(eventEnvelope.occurredAt());
    }
}
