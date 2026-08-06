package com.sample.system.reporting.service.application.mapper;

import com.sample.system.reporting.service.application.command.reporttemplate.CreateReportTemplateCommand;
import com.sample.system.reporting.service.application.command.reporttemplate.UpdateReportTemplateCommand;
import com.sample.system.reporting.service.application.response.reporttemplate.ReportTemplateResponse;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import com.sample.system.reporting.service.domain.model.valueObject.ReportTemplateId;
import org.springframework.stereotype.Component;

@Component
public class ReportTemplateMapper {
    public ReportTemplate from(CreateReportTemplateCommand command) {
        return new ReportTemplate(command.getReportDefinitionId(), command.getTemplateName(),
                command.getSheetName(), command.getColumnsJson());
    }

    public ReportTemplate from(UpdateReportTemplateCommand command) {
        ReportTemplate result = new ReportTemplate(command.getReportDefinitionId(), command.getTemplateName(),
                command.getSheetName(), command.getColumnsJson());
        result.setId(new ReportTemplateId(command.getId()));
        return result;
    }

    public ReportTemplateResponse toResponse(ReportTemplate value) {
        return new ReportTemplateResponse(value.getId() == null ? null : value.getId().getValue(),
                value.getReportDefinitionId(), value.getTemplateName(), value.getSheetName(), value.getColumnsJson());
    }
}
