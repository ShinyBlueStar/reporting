package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;

import java.util.List;

public interface IValidationRuleDefinitionRepository {

    ValidationRuleDefinition save(ValidationRuleDefinition validationRuleDefinition);

    ValidationRuleDefinition findById(Long id);

    List<ValidationRuleDefinition> findByReportDefinitionId(Long reportDefinitionId);

    void deleteById(Long id);
}
