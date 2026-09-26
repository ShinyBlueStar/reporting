package com.sample.system.reporting.service.domain.model.entity;

import java.time.Instant;

public interface ProjectionReport {

    String getAggregateId();

    void setAggregateId(String aggregateId);

    Long getAggregateVersion();

    void setAggregateVersion(Long aggregateVersion);

    String getLastEventId();

    void setLastEventId(String lastEventId);

    String getSourceService();

    void setSourceService(String sourceService);

    String getPayload();

    void setPayload(String payload);

    Instant getReportDate();

    void setReportDate(Instant reportDate);
}
