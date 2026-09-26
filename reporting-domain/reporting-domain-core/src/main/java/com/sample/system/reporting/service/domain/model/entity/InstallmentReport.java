package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.InstallmentReportId;
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
public class InstallmentReport extends AggregateRoot<InstallmentReportId> implements ProjectionReport {

    private String aggregateId;
    private Long aggregateVersion;
    private String lastEventId;
    private String sourceService;
    private String payload;
    private Long sourceInstallmentId;
    private String loanAggregateId;
    private String loanFileNo;
    private Integer installmentNo;
    private Instant dueDate;
    private BigDecimal installmentAmount;
    private BigDecimal totalPaid;
    private String installmentStatus;
    private Instant lastPaymentDate;
    private BigDecimal remainingTotal;
    private BigDecimal remainingPrincipal;
    private BigDecimal remainingInterest;
    private BigDecimal remainingPenalty;
    private BigDecimal remainingPostMaturityInterest;
    private Instant reportDate;
}
