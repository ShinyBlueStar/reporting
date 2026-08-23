package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import java.util.List;

public interface ValidationRuleDefinitionService {
    ValidationRuleDefinition create(ValidationRuleDefinition rule);
    ValidationRuleDefinition update(ValidationRuleDefinition rule);
    ValidationRuleDefinition findById(Long id);
    List<ValidationRuleDefinition> findByReportDefinitionId(Long reportDefinitionId);
    void delete(Long id);
}
