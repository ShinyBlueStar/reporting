package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.application.command.reportexecution.ExecuteReportCommand;
import com.sample.system.reporting.service.application.response.reportexecution.ExecuteReportResponse;
import com.sample.system.reporting.service.application.response.reportexecution.ReportFileDownloadResponse;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;

public interface ReportExecutionService {

    ExecuteReportResponse executeReport(ExecuteReportCommand command);

    ReportExecution findReportExecutionById(Long reportExecutionId);

    ReportFileDownloadResponse downloadReportFile(Long reportExecutionId);
}
