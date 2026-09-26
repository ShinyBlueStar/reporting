package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ReportEventId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportEvent extends AggregateRoot<ReportEventId> {

    private String eventId;
    private String eventType;
    private String aggregateType;
    private String aggregateId;
    private Long aggregateVersion;
    private String payload;
    private String sourceSystem;
    private String correlationId;
    private String status;
    private Instant eventTimestamp;
}
