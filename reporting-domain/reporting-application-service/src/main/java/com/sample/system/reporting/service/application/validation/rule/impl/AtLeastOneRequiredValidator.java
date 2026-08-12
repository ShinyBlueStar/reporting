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
public class AtLeastOneRequiredValidator extends AbstractValidationRule {

    public AtLeastOneRequiredValidator(ValidationConfigReader configReader) {
        super(configReader);
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.AT_LEAST_ONE_REQUIRED;
    }

    @Override
    protected void doValidate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        JsonNode config = configuration(definition);
        List<String> fields = readFields(config);
        boolean anyPresent = fields.stream().anyMatch(field -> isPresent(value(context, field)));
        if (!anyPresent) {
            addError(context, result, definition, fields.isEmpty() ? "parameters" : fields.getFirst());
            return;
        }
        logPassed(context, definition, "fields=" + fields + ", atLeastOnePresent=true");
    }
}
