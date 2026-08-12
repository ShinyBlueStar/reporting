package com.sample.system.reporting.service.application.validation.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.support.AbstractValidationRule;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegexValidator extends AbstractValidationRule {

    public RegexValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.REGEX;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        String field = requiredField(config);
        Object actualValue = value(context, field);
        String pattern = config.path("pattern").asText(null);
        if (isEmpty(actualValue) || pattern == null || pattern.isBlank()) {
            logSkipped(context, definition,
                    "field=" + field + " empty or pattern not configured");
            return;
        }
        if (!Pattern.compile(pattern).matcher(asString(actualValue)).matches()) {
            addError(context, result, definition, field);
            return;
        }
        logPassed(context, definition, "field=" + field + ", pattern matched");
    }
}
