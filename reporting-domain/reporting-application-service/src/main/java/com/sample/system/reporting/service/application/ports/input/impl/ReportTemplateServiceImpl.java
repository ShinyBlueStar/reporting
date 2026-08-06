package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.ReportTemplateService;
import com.sample.system.reporting.service.application.ports.output.IReportDefinitionRepository;
import com.sample.system.reporting.service.application.ports.output.IReportTemplateRepository;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportTemplateServiceImpl implements ReportTemplateService {
    private final IReportTemplateRepository repository;
    private final IReportDefinitionRepository reportDefinitionRepository;

    @Override
    @Transactional
    public ReportTemplate create(ReportTemplate template) {
        validate(template);
        ensureParentExists(template.getReportDefinitionId());
        if (repository.findByReportDefinitionIdAndTemplateName(
                template.getReportDefinitionId(), template.getTemplateName()) != null) {
            throw error(ErrorCode.REPORT_TEMPLATE_DUPLICATE);
        }
        return repository.save(template);
    }

    @Override
    @Transactional
    public ReportTemplate update(ReportTemplate template) {
        validate(template);
        if (template.getId() == null || template.getId().getValue() == null) {
            throw error(ErrorCode.INVALID_INPUT_PARAMETER);
        }
        findById(template.getId().getValue());
        ensureParentExists(template.getReportDefinitionId());
        ReportTemplate duplicate = repository.findByReportDefinitionIdAndTemplateName(
                template.getReportDefinitionId(), template.getTemplateName());
        if (duplicate != null && !duplicate.getId().getValue().equals(template.getId().getValue())) {
            throw error(ErrorCode.REPORT_TEMPLATE_DUPLICATE);
        }
        return repository.save(template);
    }

    @Override
    public ReportTemplate findById(Long id) {
        ReportTemplate result = id == null ? null : repository.findById(id);
        if (result == null) throw error(ErrorCode.REPORT_TEMPLATE_NOT_FOUND);
        return result;
    }

    @Override
    public List<ReportTemplate> findByReportDefinitionId(Long reportDefinitionId) {
        ensureParentExists(reportDefinitionId);
        return repository.findByReportDefinitionId(reportDefinitionId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    private void validate(ReportTemplate template) {
        if (template == null || template.getReportDefinitionId() == null
                || blank(template.getTemplateName()) || blank(template.getSheetName())
                || blank(template.getColumnsJson())) {
            throw error(ErrorCode.INVALID_INPUT_PARAMETER);
        }
    }

    private void ensureParentExists(Long id) {
        if (id == null || reportDefinitionRepository.findById(id) == null) {
            throw error(ErrorCode.REPORT_DEFINITION_NOT_FOUND);
        }
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
    private ReportingDomainException error(ErrorCode code) {
        return new ReportingDomainException(code.getCode(), code.name());
    }
}
