package com.sample.system.reporting.service.application.validation.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.ports.output.ValidationQueryPort;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

@Component
public class UniqueValidator extends ExistsValidator {

    public UniqueValidator(ValidationConfigReader configReader, ValidationQueryPort validationQueryPort) {
        super(configReader, validationQueryPort);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.UNIQUE;
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
        String query = requiredText(config, "query");
        long count = validationQueryPort.executeCount(query, bindParameters(config, context));
        long maximumCount = config.path("maximumCount").asLong(1L);
        if (count > maximumCount) {
            addError(context, result, definition, field);
            return;
        }
        logPassed(context, definition,
                "field=" + field + ", queryCount=" + count + ", maximumCount=" + maximumCount);
    }
}
