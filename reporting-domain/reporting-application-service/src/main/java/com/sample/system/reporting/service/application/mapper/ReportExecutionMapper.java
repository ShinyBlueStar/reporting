package com.sample.system.reporting.service.application.mapper;

import com.sample.system.reporting.service.application.response.reportexecution.ExecuteReportResponse;
import com.sample.system.reporting.service.application.response.reportexecution.FindReportExecutionResponse;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import org.springframework.stereotype.Component;

@Component
public class ReportExecutionMapper {

    public ExecuteReportResponse toExecuteResponse(ReportDefinition definition,
                                                   ReportExecution execution,
                                                   ReportFormat reportFormat,
                                                   String downloadUrl) {
        return new ExecuteReportResponse(
                execution.getId() != null ? execution.getId().getValue() : null,
                definition.getId() != null ? definition.getId().getValue() : null,
                definition.getReportCode(),
                execution.getExecutionStatus(),
                execution.getTotalRecordCount(),
                execution.getExecutionDurationMs(),
                execution.getGeneratedFileId(),
                reportFormat != null ? reportFormat.name() : null,
                downloadUrl,
                execution.getErrorMessage(),
                execution.getExecutionSource(),
                execution.getCorrelationId(),
                "Report executed successfully");
    }

    public ExecuteReportResponse toFailedExecuteResponse(ReportDefinition definition,
                                                         ReportExecution execution) {
        return new ExecuteReportResponse(
                execution.getId() != null ? execution.getId().getValue() : null,
                definition.getId() != null ? definition.getId().getValue() : null,
                definition.getReportCode(),
                execution.getExecutionStatus(),
                execution.getTotalRecordCount(),
                execution.getExecutionDurationMs(),
                execution.getGeneratedFileId(),
                null,
                null,
                execution.getErrorMessage(),
                execution.getExecutionSource(),
                execution.getCorrelationId(),
                "Report execution failed");
    }

    public FindReportExecutionResponse toFindResponse(ReportExecution execution) {
        return new FindReportExecutionResponse(
                execution.getId() != null ? execution.getId().getValue() : null,
                execution.getReportDefinitionId(),
                execution.getExecutionStatus(),
                execution.getExecutionStartTime(),
                execution.getExecutionEndTime(),
                execution.getExecutionDurationMs(),
                execution.getTotalRecordCount(),
                execution.getRequestedBy(),
                execution.getRequestParameters(),
                execution.getGeneratedFileId(),
                execution.getErrorMessage(),
                execution.getExecutionSource(),
                execution.getCorrelationId());
    }
}
