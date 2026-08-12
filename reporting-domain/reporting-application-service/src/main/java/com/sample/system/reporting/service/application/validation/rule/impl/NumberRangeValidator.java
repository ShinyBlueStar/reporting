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
public class NumberRangeValidator extends AbstractValidationRule {

    public NumberRangeValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.NUMBER_RANGE;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        String field = requiredField(config);
        Object actualValue = value(context, field);
        if (isEmpty(actualValue) || !config.has("min") || !config.has("max")) {
            logSkipped(context, definition,
                    "field=" + field + " empty or min/max not configured");
            return;
        }
        BigDecimal actual = asNumber(actualValue);
        BigDecimal min = config.get("min").decimalValue();
        BigDecimal max = config.get("max").decimalValue();
        if (actual == null || actual.compareTo(min) < 0 || actual.compareTo(max) > 0) {
            addError(context, result, definition, field);
            return;
        }
        logPassed(context, definition,
                "field=" + field + ", value=" + actual + ", min=" + min + ", max=" + max);
    }
}
