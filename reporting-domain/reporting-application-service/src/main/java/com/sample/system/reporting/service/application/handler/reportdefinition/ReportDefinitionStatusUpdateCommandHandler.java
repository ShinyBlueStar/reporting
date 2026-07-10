package com.sample.system.reporting.service.application.handler.reportdefinition;

import com.sample.system.reporting.service.application.command.reportdefinition.UpdateReportDefinitionStatusCommand;
import com.sample.system.reporting.service.application.mapper.ReportDefinitionMapper;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.response.reportdefinition.UpdateReportDefinitionResponse;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportDefinitionStatusUpdateCommandHandler {

    private final ReportDefinitionService reportDefinitionService;
    private final ReportDefinitionMapper reportDefinitionMapper;

    public UpdateReportDefinitionResponse updateStatus(UpdateReportDefinitionStatusCommand command) {
        ReportDefinition updated = reportDefinitionService.updateReportDefinitionStatus(
                command.getReportDefinitionId(), command.getActive());
        return reportDefinitionMapper.toUpdateResponse(updated);
    }
}
