package com.sample.system.reporting.service.application.handler.reportdefinition;

import com.sample.system.reporting.service.application.command.reportdefinition.FindReportDefinitionCommand;
import com.sample.system.reporting.service.application.mapper.ReportDefinitionMapper;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.response.reportdefinition.FindReportDefinitionResponse;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportDefinitionFindCommandHandler {
    private final ReportDefinitionService reportDefinitionService;
    private final ReportDefinitionMapper reportDefinitionMapper;

    public FindReportDefinitionResponse findReportDefinition(FindReportDefinitionCommand command) {
        boolean hasId = command.getReportDefinitionId() != null;
        boolean hasCode = command.getReportDefinitionCode() != null
                && !command.getReportDefinitionCode().isBlank();
        if (hasId == hasCode) {
            throw new ReportingDomainException(
                    ErrorCode.INVALID_INPUT_PARAMETER.getCode(),
                    "Exactly one of reportDefinitionId or reportDefinitionCode is required");
        }
        ReportDefinition definition = hasId
                ? reportDefinitionService.findReportDefinitionById(command.getReportDefinitionId())
                : reportDefinitionService.findReportDefinitionByCode(command.getReportDefinitionCode());
        return reportDefinitionMapper.toFindResponse(definition);
    }
}
