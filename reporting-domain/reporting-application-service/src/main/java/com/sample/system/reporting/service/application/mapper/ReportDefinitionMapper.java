package com.sample.system.reporting.service.application.mapper;

import com.sample.system.reporting.service.application.command.reportdefinition.CreateReportDefinitionCommand;
import com.sample.system.reporting.service.application.command.reportdefinition.SearchReportDefinitionCommand;
import com.sample.system.reporting.service.application.command.reportdefinition.UpdateReportDefinitionCommand;
import com.sample.system.reporting.service.application.response.reportdefinition.CreateReportDefinitionResponse;
import com.sample.system.reporting.service.application.response.reportdefinition.FindReportDefinitionResponse;
import com.sample.system.reporting.service.application.response.reportdefinition.UpdateReportDefinitionResponse;
import com.sample.system.reporting.service.domain.model.ReportDefinitionSearchCriteria;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.valueObject.ReportDefinitionId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportDefinitionMapper {

    private static final long DEFAULT_MAX_EXPORT_ROWS = 20_000L;

    public ReportDefinition createCommandToReportDefinition(CreateReportDefinitionCommand command) {
        ReportDefinition definition = new ReportDefinition();
        definition.setReportCode(command.getReportDefinitionCode());
        definition.setReportName(command.getReportName());
        definition.setReportDescription(command.getReportDescription());
        definition.setCategoryId(command.getCategoryId());
        definition.setSqlQuery(command.getSqlQuery());
        definition.setTimeoutSeconds(command.getTimeoutSeconds());
        definition.setActive(command.getActive() != null ? command.getActive() : Boolean.TRUE);
        definition.setVersion(command.getVersion() != null ? command.getVersion() : 0L);
        definition.setReportType(command.getReportType());
        definition.setMaxExportRows(command.getMaxExportRows() != null
                ? command.getMaxExportRows()
                : DEFAULT_MAX_EXPORT_ROWS);
        return definition;
    }

    public ReportDefinition updateCommandToReportDefinition(UpdateReportDefinitionCommand command,
                                                            ReportDefinition existing) {
        ReportDefinition definition = new ReportDefinition();
        definition.setId(new ReportDefinitionId(command.getReportDefinitionId()));
        definition.setReportCode(command.getReportDefinitionCode());
        definition.setReportName(command.getReportName());
        definition.setReportDescription(command.getReportDescription());
        definition.setCategoryId(command.getCategoryId());
        definition.setCategoryName(existing.getCategoryName());
        definition.setSqlQuery(command.getSqlQuery());
        definition.setTimeoutSeconds(command.getTimeoutSeconds());
        definition.setActive(command.getActive() != null ? command.getActive() : existing.getActive());
        definition.setVersion(command.getVersion() != null ? command.getVersion() : existing.getVersion());
        definition.setReportType(command.getReportType() != null ? command.getReportType() : existing.getReportType());
        definition.setMaxExportRows(
                command.getMaxExportRows() != null ? command.getMaxExportRows() : existing.getMaxExportRows());
        if (definition.getMaxExportRows() == null) {
            definition.setMaxExportRows(DEFAULT_MAX_EXPORT_ROWS);
        }
        return definition;
    }

    public ReportDefinitionSearchCriteria searchCommandToCriteria(SearchReportDefinitionCommand command) {
        ReportDefinitionSearchCriteria criteria = new ReportDefinitionSearchCriteria();
        criteria.setReportCode(command.getReportDefinitionCode());
        criteria.setReportName(command.getReportName());
        criteria.setCategoryId(command.getCategoryId());
        criteria.setCategoryName(command.getCategoryName());
        criteria.setReportType(command.getReportType());
        criteria.setActive(command.getActive());
        criteria.setPage(command.getPage());
        criteria.setSize(command.getSize());
        return criteria;
    }

    public CreateReportDefinitionResponse toCreateResponse(ReportDefinition saved) {
        return new CreateReportDefinitionResponse(
                saved.getId() != null ? saved.getId().getValue() : null,
                "Report definition created successfully");
    }

    public UpdateReportDefinitionResponse toUpdateResponse(ReportDefinition updated) {
        return new UpdateReportDefinitionResponse(
                updated.getId() != null ? updated.getId().getValue() : null,
                "Report definition updated successfully");
    }

    public FindReportDefinitionResponse toFindResponse(ReportDefinition definition) {
        return new FindReportDefinitionResponse(
                definition.getId() != null ? definition.getId().getValue() : null,
                definition.getReportCode(),
                definition.getReportName(),
                definition.getReportDescription(),
                definition.getCategoryId(),
                definition.getCategoryName(),
                definition.getSqlQuery(),
                definition.getTimeoutSeconds(),
                definition.getActive(),
                definition.getVersion(),
                definition.getReportType(),
                definition.getMaxExportRows());
    }

    public List<FindReportDefinitionResponse> toFindResponses(List<ReportDefinition> definitions) {
        return definitions.stream().map(this::toFindResponse).toList();
    }
}
