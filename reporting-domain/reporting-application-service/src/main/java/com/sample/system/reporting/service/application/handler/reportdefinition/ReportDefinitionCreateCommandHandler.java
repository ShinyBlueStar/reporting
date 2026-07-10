package com.sample.system.reporting.service.application.handler.reportdefinition;

import com.sample.system.reporting.service.application.command.reportdefinition.CreateReportDefinitionCommand;
import com.sample.system.reporting.service.application.mapper.ReportDefinitionMapper;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.response.reportdefinition.CreateReportDefinitionResponse;
import com.sample.system.reporting.service.application.reportexecution.ReportSqlGuard;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportDefinitionCreateCommandHandler {

    private final ReportDefinitionService reportDefinitionService;
    private final ReportDefinitionMapper reportDefinitionMapper;
    private final ReportSqlGuard reportSqlGuard;

    public CreateReportDefinitionResponse createReportDefinition(CreateReportDefinitionCommand command) {
        ReportDefinition definition = reportDefinitionMapper.createCommandToReportDefinition(command);
        reportSqlGuard.ensureSelectableQuery(definition.getSqlQuery());
        ReportDefinition saved = reportDefinitionService.createReportDefinition(definition);
        return reportDefinitionMapper.toCreateResponse(saved);
    }
}
