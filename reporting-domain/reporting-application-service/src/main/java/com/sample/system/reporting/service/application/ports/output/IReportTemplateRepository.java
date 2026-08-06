package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;

public interface IReportTemplateRepository {

    ReportTemplate save(ReportTemplate reportTemplate);

    ReportTemplate findById(Long id);

    ReportTemplate findDefaultByReportDefinitionId(Long reportDefinitionId);

    ReportTemplate findByReportDefinitionIdAndTemplateName(Long reportDefinitionId, String templateName);

    java.util.List<ReportTemplate> findByReportDefinitionId(Long reportDefinitionId);

    void deleteById(Long id);
}
