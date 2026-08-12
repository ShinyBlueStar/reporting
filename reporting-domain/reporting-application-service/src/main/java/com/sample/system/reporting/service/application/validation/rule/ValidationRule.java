package com.sample.system.reporting.service.application.validation.rule;

import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;

public interface ValidationRule {

    ValidationRuleType getType();

    void validate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result);
}
