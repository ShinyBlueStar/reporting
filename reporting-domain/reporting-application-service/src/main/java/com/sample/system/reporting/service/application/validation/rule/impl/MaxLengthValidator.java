package com.sample.system.reporting.service.application.validation.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.support.AbstractValidationRule;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

@Component
public class MaxLengthValidator extends AbstractValidationRule {

    public MaxLengthValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.MAX_LENGTH;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        String field = requiredField(config);
        Object actualValue = value(context, field);
        if (isEmpty(actualValue) || !config.has("length")) {
            logSkipped(context, definition,
                    "field=" + field + " empty or max length not configured");
            return;
        }
        int maxLength = config.get("length").asInt();
        int actualLength = asString(actualValue).length();
        if (actualLength > maxLength) {
            addError(context, result, definition, field);
            return;
        }
        logPassed(context, definition,
                "field=" + field + ", length=" + actualLength + ", maxLength=" + maxLength);
    }
}
