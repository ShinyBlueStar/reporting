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
public class ExactlyOneValidator extends AbstractValidationRule {

    public ExactlyOneValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.EXACTLY_ONE;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        List<String> fields = readFields(config);
        long presentCount = fields.stream().filter(field -> isPresent(value(context, field))).count();
        if (presentCount != 1) {
            addError(context, result, definition, fields.isEmpty() ? "parameters" : fields.getFirst());
            return;
        }
        logPassed(context, definition, "fields=" + fields + ", presentCount=1");
    }
}
