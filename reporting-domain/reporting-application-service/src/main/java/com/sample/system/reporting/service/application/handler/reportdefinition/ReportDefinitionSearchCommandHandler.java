package com.sample.system.reporting.service.application.handler.reportdefinition;

import com.sample.system.reporting.service.application.command.reportdefinition.SearchReportDefinitionCommand;
import com.sample.system.reporting.service.application.mapper.ReportDefinitionMapper;
import com.sample.system.reporting.service.application.ports.input.ReportDefinitionService;
import com.sample.system.reporting.service.application.response.reportdefinition.FindReportDefinitionResponse;
import com.sample.system.reporting.service.application.response.reportdefinition.SearchReportDefinitionResponse;
import com.sample.system.reporting.service.domain.model.ReportDefinitionSearchCriteria;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportDefinitionSearchCommandHandler {

    private final ReportDefinitionService reportDefinitionService;
    private final ReportDefinitionMapper reportDefinitionMapper;

    public SearchReportDefinitionResponse<FindReportDefinitionResponse> searchReportDefinitions(
            SearchReportDefinitionCommand command) {
        ReportDefinitionSearchCriteria criteria = reportDefinitionMapper.searchCommandToCriteria(command);
        Page<ReportDefinition> page = reportDefinitionService.searchReportDefinitions(criteria);
        return new SearchReportDefinitionResponse<>(
                reportDefinitionMapper.toFindResponses(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
