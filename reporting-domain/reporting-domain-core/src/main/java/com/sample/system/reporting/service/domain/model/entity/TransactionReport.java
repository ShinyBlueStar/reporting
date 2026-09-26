package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.TransactionReportId;
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
public class TransactionReport extends AggregateRoot<TransactionReportId> implements ProjectionReport {

    private String aggregateId;
    private Long aggregateVersion;
    private String lastEventId;
    private String sourceService;
    private String payload;
    private String accountNumber;
    private String transactionRef;
    private String transactionType;
    private BigDecimal amount;
    private Instant transactionDate;
    private String status;
    private Instant reportDate;
}
