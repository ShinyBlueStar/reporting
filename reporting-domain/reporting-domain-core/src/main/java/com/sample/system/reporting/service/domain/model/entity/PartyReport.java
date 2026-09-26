package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.PartyReportId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartyReport extends AggregateRoot<PartyReportId> implements ProjectionReport {

    private String aggregateId;
    private Long aggregateVersion;
    private String lastEventId;
    private String sourceService;
    private String payload;
    private String nationalCode;
    private String partyNo;
    private String partyName;
    private String partyFamily;
    private String mobile;
    private String mobileNumber;
    private String email;
    private String status;
    private Instant reportDate;
}
