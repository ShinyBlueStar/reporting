package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.IReportTemplateRepository;
import com.sample.system.reporting.service.dataaccess.mapper.ReportTemplateDataAccessMapper;
import com.sample.system.reporting.service.dataaccess.repository.ReportTemplateRepository;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportTemplatePersistenceAdapter implements IReportTemplateRepository {

    private final ReportTemplateRepository reportTemplateRepository;
    private final ReportTemplateDataAccessMapper reportTemplateDataAccessMapper;

    @Override
    public ReportTemplate save(ReportTemplate reportTemplate) {
        return reportTemplateDataAccessMapper.reportTemplateEntityToReportTemplate(
                reportTemplateRepository.save(
                        reportTemplateDataAccessMapper.reportTemplateToReportTemplateEntity(reportTemplate)));
    }

    @Override
    public ReportTemplate findById(Long id) {
        return reportTemplateRepository.findById(id)
                .map(reportTemplateDataAccessMapper::reportTemplateEntityToReportTemplate)
                .orElse(null);
    }

    @Override
    public ReportTemplate findDefaultByReportDefinitionId(Long reportDefinitionId) {
        return reportTemplateRepository.findFirstByReportDefinition_IdOrderByIdAsc(reportDefinitionId)
                .map(reportTemplateDataAccessMapper::reportTemplateEntityToReportTemplate)
                .orElse(null);
    }

    @Override
    public ReportTemplate findByReportDefinitionIdAndTemplateName(Long reportDefinitionId, String templateName) {
        if (templateName == null || templateName.isBlank()) {
            return findDefaultByReportDefinitionId(reportDefinitionId);
        }
        return reportTemplateRepository.findByReportDefinition_IdAndTemplateName(reportDefinitionId, templateName)
                .map(reportTemplateDataAccessMapper::reportTemplateEntityToReportTemplate)
                .orElse(null);
    }

    @Override
    public java.util.List<ReportTemplate> findByReportDefinitionId(Long reportDefinitionId) {
        return reportTemplateRepository.findByReportDefinition_IdOrderByIdAsc(reportDefinitionId)
                .stream()
                .map(reportTemplateDataAccessMapper::reportTemplateEntityToReportTemplate)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        reportTemplateRepository.deleteById(id);
    }
}
