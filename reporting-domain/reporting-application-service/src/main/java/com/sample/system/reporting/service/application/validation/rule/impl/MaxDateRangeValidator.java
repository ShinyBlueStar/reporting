package com.sample.system.reporting.service.application.validation.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.support.AbstractValidationRule;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MaxDateRangeValidator extends AbstractValidationRule {

    public MaxDateRangeValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.MAX_DATE_RANGE;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        String fromField = requiredText(config, "fromField");
        String toField = requiredText(config, "toField");
        Object fromValue = value(context, fromField);
        Object toValue = value(context, toField);
        if (isEmpty(fromValue) || isEmpty(toValue) || !config.has("maxDays")) {
            logSkipped(context, definition,
                    "max date range not evaluated: fromField=" + fromField + ", toField=" + toField);
            return;
        }
        LocalDate from = asLocalDate(fromValue);
        LocalDate to = asLocalDate(toValue);
        long maxDays = config.get("maxDays").asLong();
        long actualDays = daysBetween(from, to);
        if (actualDays > maxDays) {
            addError(context, result, definition, fromField);
            return;
        }
        logPassed(context, definition,
                "fromField=" + fromField + ", toField=" + toField
                        + ", actualDays=" + actualDays + ", maxDays=" + maxDays);
    }
}
