package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.ValidationRuleDefinitionService;
import com.sample.system.reporting.service.application.ports.output.IReportDefinitionRepository;
import com.sample.system.reporting.service.application.ports.output.IValidationRuleDefinitionRepository;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidationRuleDefinitionServiceImpl implements ValidationRuleDefinitionService {
    private final IValidationRuleDefinitionRepository repository;
    private final IReportDefinitionRepository reportDefinitionRepository;

    @Override
    @Transactional
    public ValidationRuleDefinition create(ValidationRuleDefinition rule) {
        validate(rule);
        ensureParentExists(rule.getReportDefinitionId());
        return repository.save(rule);
    }

    @Override
    @Transactional
    public ValidationRuleDefinition update(ValidationRuleDefinition rule) {
        validate(rule);
        if (rule.getId() == null || rule.getId().getValue() == null) {
            throw error(ErrorCode.INVALID_INPUT_PARAMETER);
        }
        findById(rule.getId().getValue());
        ensureParentExists(rule.getReportDefinitionId());
        return repository.save(rule);
    }

    @Override
    public ValidationRuleDefinition findById(Long id) {
        ValidationRuleDefinition result = id == null ? null : repository.findById(id);
        if (result == null) throw error(ErrorCode.VALIDATION_RULE_NOT_FOUND);
        return result;
    }

    @Override
    public List<ValidationRuleDefinition> findByReportDefinitionId(Long reportDefinitionId) {
        ensureParentExists(reportDefinitionId);
        return repository.findByReportDefinitionId(reportDefinitionId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    private void validate(ValidationRuleDefinition rule) {
        if (rule == null || rule.getReportDefinitionId() == null || rule.getValidationRuleType() == null
                || blank(rule.getConfiguration()) || blank(rule.getErrorCode()) || blank(rule.getErrorMessage())) {
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
