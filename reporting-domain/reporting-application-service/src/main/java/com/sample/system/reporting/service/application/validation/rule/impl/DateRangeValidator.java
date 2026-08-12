package com.sample.system.reporting.service.application.validation.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.support.AbstractValidationRule;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DateRangeValidator extends AbstractValidationRule {

    public DateRangeValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.DATE_RANGE;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        String fromField = requiredText(config, "fromField");
        String toField = requiredText(config, "toField");
        Object fromValue = value(context, fromField);
        Object toValue = value(context, toField);
        if (isEmpty(fromValue) || isEmpty(toValue)) {
            logSkipped(context, definition,
                    "date range not evaluated because fromField or toField is empty: fromField="
                            + fromField + ", toField=" + toField);
            return;
        }
        Instant from = requireInstant(fromValue, fromField);
        Instant to = requireInstant(toValue, toField);
        if (from.isAfter(to)) {
            addError(context, result, definition, fromField);
            return;
        }
        logPassed(context, definition,
                "fromField=" + fromField + ", toField=" + toField + ", from=" + from + ", to=" + to);
    }
}
