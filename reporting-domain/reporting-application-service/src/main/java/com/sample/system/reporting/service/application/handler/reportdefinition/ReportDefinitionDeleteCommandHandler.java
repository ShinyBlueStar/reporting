package com.sample.system.reporting.service.application.handler.reportdefinition;

import com.sample.system.reporting.service.application.command.reportdefinition.DeleteReportDefinitionCommand;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.response.reportdefinition.DeleteReportDefinitionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportDefinitionDeleteCommandHandler {

    private final ReportDefinitionService reportDefinitionService;

    public DeleteReportDefinitionResponse deleteReportDefinition(DeleteReportDefinitionCommand command) {
        reportDefinitionService.deleteReportDefinition(command.getReportDefinitionId());
        return new DeleteReportDefinitionResponse(
                command.getReportDefinitionId(),
                "Report definition deleted successfully");
    }
}
