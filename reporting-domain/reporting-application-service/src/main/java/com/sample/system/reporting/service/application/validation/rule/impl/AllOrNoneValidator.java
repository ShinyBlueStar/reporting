package com.sample.system.reporting.service.application.validation.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.support.AbstractValidationRule;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllOrNoneValidator extends AbstractValidationRule {

    public AllOrNoneValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.ALL_OR_NONE;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        List<String> fields = readFields(config);
        long presentCount = fields.stream().filter(field -> isPresent(value(context, field))).count();
        if (presentCount > 0 && presentCount < fields.size()) {
            addError(context, result, definition, fields.getFirst());
            return;
        }
        logPassed(context, definition, "fields=" + fields + ", presentCount=" + presentCount);
    }
}
