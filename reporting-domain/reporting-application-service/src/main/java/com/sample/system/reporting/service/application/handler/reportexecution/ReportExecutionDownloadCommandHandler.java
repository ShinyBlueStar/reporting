package com.sample.system.reporting.service.application.handler.reportexecution;

import com.sample.system.reporting.service.application.ports.input.ReportExecutionService;
import com.sample.system.reporting.service.application.response.reportexecution.ReportFileDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportExecutionDownloadCommandHandler {

    private final ReportExecutionService reportExecutionService;

    public ReportFileDownloadResponse downloadReportFile(Long reportExecutionId) {
        return reportExecutionService.downloadReportFile(reportExecutionId);
    }
}
