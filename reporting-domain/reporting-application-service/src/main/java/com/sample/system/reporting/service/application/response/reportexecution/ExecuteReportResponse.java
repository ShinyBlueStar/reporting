package com.sample.system.reporting.service.application.response.reportexecution;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecuteReportResponse {

    private final Long reportExecutionCode;
    private final Long reportDefinitionId;
    private final String reportDefinitionCode;
    private final String executionStatus;
    private final Long totalRecordCount;
    private final Long executionDurationMs;
    private final Long generatedFileId;
    private final String reportFormat;
    private final String downloadUrl;
    private final String errorMessage;
    private final String executionSource;
    private final String correlationId;
    private final String message;
}
