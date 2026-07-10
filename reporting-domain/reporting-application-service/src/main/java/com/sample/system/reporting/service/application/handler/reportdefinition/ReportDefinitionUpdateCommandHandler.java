package com.sample.system.reporting.service.application.handler.reportdefinition;

import com.sample.system.reporting.service.application.command.reportdefinition.UpdateReportDefinitionCommand;
import com.sample.system.reporting.service.application.mapper.ReportDefinitionMapper;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.response.reportdefinition.UpdateReportDefinitionResponse;
import com.sample.system.reporting.service.application.reportexecution.ReportSqlGuard;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportDefinitionUpdateCommandHandler {

    private final ReportDefinitionService reportDefinitionService;
    private final ReportDefinitionMapper reportDefinitionMapper;
    private final ReportSqlGuard reportSqlGuard;

    public UpdateReportDefinitionResponse updateReportDefinition(UpdateReportDefinitionCommand command) {
        ReportDefinition existing = reportDefinitionService.findReportDefinitionById(command.getReportDefinitionId());
        ReportDefinition definition = reportDefinitionMapper.updateCommandToReportDefinition(command, existing);
        reportSqlGuard.ensureSelectableQuery(definition.getSqlQuery());
        ReportDefinition updated = reportDefinitionService.updateReportDefinition(definition);
        return reportDefinitionMapper.toUpdateResponse(updated);
    }
}
