package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.LoanReportId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanReport extends AggregateRoot<LoanReportId> implements ProjectionReport {

    private String aggregateId;
    private Long aggregateVersion;
    private String lastEventId;
    private String sourceService;
    private String payload;
    private Long sourceLoanId;
    private Long partyId;
    private String nationalCode;
    private String nationalName;
    private String fileNo;
    private String loanFileNo;
    private String loanProfileName;
    private String unitCode;
    private String unitName;
    private Instant creationDate;
    private BigDecimal requestedAmount;
    private BigDecimal totalInterest;
    private Integer installmentCount;
    private Integer paidInstallmentCount;
    private Instant firstInstallmentDate;
    private Instant lastInstallmentDate;
    private String loanStatus;
    private BigDecimal totalRemainingPrincipal;
    private BigDecimal totalRemainingInterest;
    private BigDecimal totalRemainingPenalty;
    private BigDecimal totalRemainingPostMaturityInterest;
    private BigDecimal totalRemainingBeforeDueDate;
    private BigDecimal totalRemainingPastDueDate;
    private Instant reportDate;
}
