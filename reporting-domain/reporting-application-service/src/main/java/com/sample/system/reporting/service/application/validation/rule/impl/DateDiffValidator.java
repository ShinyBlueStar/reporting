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
import java.util.Locale;

@Component
public class DateDiffValidator extends AbstractValidationRule {

    public DateDiffValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.DATE_DIFF;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        String fromField = requiredText(config, "fromField");
        String toField = requiredText(config, "toField");
        Object fromValue = value(context, fromField);
        Object toValue = value(context, toField);
        if (isEmpty(fromValue) || isEmpty(toValue) || !config.has("days")) {
            logSkipped(context, definition,
                    "date diff not evaluated: fromField=" + fromField + ", toField=" + toField);
            return;
        }
        long actualDays = daysBetween(asLocalDate(fromValue), asLocalDate(toValue));
        long expectedDays = config.get("days").asLong();
        String operator = config.path("operator").asText("EQUAL").toUpperCase(Locale.ROOT);
        boolean valid = switch (operator) {
            case "GREATER_THAN" -> actualDays > expectedDays;
            case "LESS_THAN" -> actualDays < expectedDays;
            case "GREATER_THAN_OR_EQUAL" -> actualDays >= expectedDays;
            case "LESS_THAN_OR_EQUAL" -> actualDays <= expectedDays;
            default -> actualDays == expectedDays;
        };
        if (!valid) {
            addError(context, result, definition, fromField);
            return;
        }
        logPassed(context, definition,
                "fromField=" + fromField + ", toField=" + toField
                        + ", actualDays=" + actualDays + ", expectedDays=" + expectedDays
                        + ", operator=" + operator);
    }
}
