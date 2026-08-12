package com.sample.system.reporting.service.application.validation.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.support.AbstractValidationRule;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PositiveValidator extends AbstractValidationRule {

    public PositiveValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.POSITIVE;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        String field = requiredField(config);
        Object actualValue = value(context, field);
        if (isEmpty(actualValue)) {
            logSkipped(context, definition, "field=" + field + " is empty");
            return;
        }
        BigDecimal actual = asNumber(actualValue);
        if (actual == null || actual.compareTo(BigDecimal.ZERO) <= 0) {
            addError(context, result, definition, field);
            return;
        }
        logPassed(context, definition, "field=" + field + ", value=" + actual + " is positive");
    }
}
