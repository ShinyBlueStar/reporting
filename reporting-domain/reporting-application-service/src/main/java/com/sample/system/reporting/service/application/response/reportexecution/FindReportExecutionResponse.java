package com.sample.system.reporting.service.application.response.reportexecution;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FindReportExecutionResponse {

    private final Long reportExecutionCode;
    private final Long reportDefinitionId;
    private final String executionStatus;
    private final Instant executionStartTime;
    private final Instant executionEndTime;
    private final Long executionDurationMs;
    private final Long totalRecordCount;
    private final String requestedBy;
    private final String requestParameters;
    private final Long generatedFileId;
    private final String errorMessage;
    private final String executionSource;
    private final String correlationId;
}
