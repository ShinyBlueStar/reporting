package com.sample.system.reporting.service.application.validation.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.ports.output.ValidationQueryPort;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.support.AbstractValidationRule;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class ExistsValidator extends AbstractValidationRule {

    protected final ValidationQueryPort validationQueryPort;

    public ExistsValidator(ValidationConfigReader configReader, ValidationQueryPort validationQueryPort) {
        super(configReader);
        this.validationQueryPort = validationQueryPort;
    }

    @Override
    public ValidationRuleType getType() {
        return ValidationRuleType.EXISTS;
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
        long minimumCount = config.path("minimumCount").asLong(1L);
        if (count < minimumCount) {
            addError(context, result, definition, field);
            return;
        }
        logPassed(context, definition,
                "field=" + field + ", queryCount=" + count + ", minimumCount=" + minimumCount);
    }

    protected Map<String, Object> bindParameters(JsonNode config, ValidationContext context) {
        Map<String, Object> bindings = new HashMap<>(context.parameters());
        JsonNode bindingsNode = config.get("bindings");
        if (bindingsNode != null && bindingsNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = bindingsNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                bindings.put(entry.getKey(), value(context, entry.getValue().asText()));
            }
        }
        return bindings;
    }
}
