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
public class NotInListValidator extends AbstractValidationRule {

    public NotInListValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.NOT_IN_LIST;
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
        JsonNode valuesNode = config.get("values");
        if (valuesNode == null || !valuesNode.isArray()) {
            logSkipped(context, definition, "field=" + field + ", forbidden values not configured");
            return;
        }
        String actualText = asString(actualValue);
        for (JsonNode valueNode : valuesNode) {
            if (valueNode.asText().equals(actualText)) {
                addError(context, result, definition, field);
                return;
            }
        }
        logPassed(context, definition, "field=" + field + ", value=" + actualText + " is not in forbidden list");
    }
}
