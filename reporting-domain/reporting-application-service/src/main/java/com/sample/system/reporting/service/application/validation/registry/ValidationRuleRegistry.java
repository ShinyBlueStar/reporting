package com.sample.system.reporting.service.application.validation.registry;

import com.sample.system.reporting.service.application.validation.rule.ValidationRule;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ValidationRuleRegistry {

    private final Map<ValidationRuleType, ValidationRule> rulesByType;

    public ValidationRuleRegistry(List<ValidationRule> rules) {
        this.rulesByType = rules.stream()
                .collect(Collectors.toUnmodifiableMap(ValidationRule::getType, Function.identity()));
    }

    public ValidationRule get(ValidationRuleType type) {
        ValidationRule rule = rulesByType.get(type);
        if (rule == null) {
            throw new ReportingDomainException(
                    ErrorCode.INVALID_INPUT_PARAMETER.getCode(),
                    "No validator registered for rule type: " + type);
        }
        return rule;
    }
}
