package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ReportExecutionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportExecution extends AggregateRoot<ReportExecutionId> {

    private Long reportDefinitionId;
    private String executionStatus;
    private Instant executionStartTime;
    private Instant executionEndTime;
    private Long executionDurationMs;
    private Long totalRecordCount;
    private String requestedBy;
    private String requestParameters;
    private Long generatedFileId;
    private String errorMessage;
    private String executionSource;
    private String correlationId;
}
