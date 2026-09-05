package com.sample.system.reporting.service.application.handler.reportexecution;

import com.sample.system.reporting.service.application.command.reportexecution.ExecuteReportCommand;
import com.sample.system.reporting.service.application.ports.input.ReportExecutionService;
import com.sample.system.reporting.service.application.response.reportexecution.ExecuteReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportExecutionExecuteCommandHandler {

    private final ReportExecutionService reportExecutionService;

    public ExecuteReportResponse executeReport(ExecuteReportCommand command) {
        return reportExecutionService.executeReport(command);
    }
}
