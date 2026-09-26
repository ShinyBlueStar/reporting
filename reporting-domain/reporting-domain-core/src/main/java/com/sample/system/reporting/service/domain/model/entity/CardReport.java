package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.CardReportId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardReport extends AggregateRoot<CardReportId> implements ProjectionReport {

    private String aggregateId;
    private Long aggregateVersion;
    private String lastEventId;
    private String sourceService;
    private String payload;
    private String nationalCode;
    private String cardNumber;
    private String accountNumber;
    private String cardStatus;
    private String status;
    private Instant reportDate;
}
