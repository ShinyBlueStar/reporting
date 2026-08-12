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
public class RequiredIfValidator extends AbstractValidationRule {

    public RequiredIfValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.REQUIRED_IF;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        if (!matchesCondition(context, config)) {
            logSkipped(context, definition, "condition not matched");
            return;
        }
        String field = requiredField(config);
        if (isEmpty(value(context, field))) {
            addError(context, result, definition, field);
            return;
        }
        logPassed(context, definition, "condition matched, field=" + field + " is present");
    }
}
