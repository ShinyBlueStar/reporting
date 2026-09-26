package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.AccountReportId;
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
public class AccountReport extends AggregateRoot<AccountReportId> implements ProjectionReport {

    private String aggregateId;
    private Long aggregateVersion;
    private String lastEventId;
    private String sourceService;
    private String payload;
    private String nationalCode;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private Instant reportDate;
}
