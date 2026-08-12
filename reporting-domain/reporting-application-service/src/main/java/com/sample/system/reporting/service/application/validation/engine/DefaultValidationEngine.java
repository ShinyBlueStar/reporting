package com.sample.system.reporting.service.application.validation.engine;

import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.registry.ValidationRuleRegistry;
import com.sample.system.reporting.service.application.validation.rule.ValidationRule;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DefaultValidationEngine implements ValidationEngine {

    private final ValidationRuleRegistry validationRuleRegistry;

    public DefaultValidationEngine(ValidationRuleRegistry validationRuleRegistry) {
        this.validationRuleRegistry = validationRuleRegistry;
    }

    @Override
    public void validate(ValidationContext context, List<ValidationRuleDefinition> rules) {
        ValidationResult result = new ValidationResult();
        rules.stream()
                .filter(this::isEnabled)
                .sorted(Comparator.comparing(
                        ValidationRuleDefinition::getExecutionOrder,
                        Comparator.nullsLast(Integer::compareTo)))
                .forEach(ruleDefinition -> executeRule(context, ruleDefinition, result));

        if (result.hasErrors()) {
            throw ReportingDomainException.validationFailed(result.getErrors());
        }
    }

    private void executeRule(
            ValidationContext context,
            ValidationRuleDefinition ruleDefinition,
            ValidationResult result) {
        ValidationRule rule = validationRuleRegistry.get(ruleDefinition.getValidationRuleType());
        rule.validate(context, ruleDefinition, result);
    }

    private boolean isEnabled(ValidationRuleDefinition ruleDefinition) {
        return ruleDefinition.getEnabled() == null || Boolean.TRUE.equals(ruleDefinition.getEnabled());
    }
}
