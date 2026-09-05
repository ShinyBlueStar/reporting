package com.sample.system.reporting.service.application.handler.reportexecution;

import com.sample.system.reporting.service.application.command.reportexecution.FindReportExecutionByIdCommand;
import com.sample.system.reporting.service.application.mapper.ReportExecutionMapper;
import com.sample.system.reporting.service.application.ports.input.ReportExecutionService;
import com.sample.system.reporting.service.application.response.reportexecution.FindReportExecutionResponse;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportExecutionFindByIdCommandHandler {

    private final ReportExecutionService reportExecutionService;
    private final ReportExecutionMapper reportExecutionMapper;

    public FindReportExecutionResponse findReportExecutionById(FindReportExecutionByIdCommand command) {
        ReportExecution execution = reportExecutionService.findReportExecutionById(command.getReportExecutionCode());
        return reportExecutionMapper.toFindResponse(execution);
    }
}
