package com.sample.system.reporting.service.application.validation.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.utility.ReportInstantParser;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.model.ValidationResult;
import com.sample.system.reporting.service.application.validation.rule.ValidationRule;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractValidationRule implements ValidationRule {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final ValidationConfigReader configReader;

    private boolean detailedPassLogged;
    private boolean skipped;

    protected AbstractValidationRule(ValidationConfigReader configReader) {
        this.configReader = configReader;
    }

    @Override
    public final void validate(ValidationContext context, ValidationRuleDefinition definition, ValidationResult result) {
        detailedPassLogged = false;
        skipped = false;
        log.info("[Validation] Executing {} rule. reportCode={}, ruleId={}, executionOrder={}",
                getType(),
                reportCode(context),
                ruleId(definition),
                definition.getExecutionOrder());
        int errorsBefore = result.getErrors().size();
        doValidate(context, definition, result);
        if (result.getErrors().size() > errorsBefore || skipped) {
            return;
        }
        if (!detailedPassLogged) {
            log.info("[Validation] {} rule passed. reportCode={}, ruleId={}",
                    getType(),
                    reportCode(context),
                    ruleId(definition));
        }
    }

    protected abstract void doValidate(
            ValidationContext context,
            ValidationRuleDefinition definition,
            ValidationResult result);

    protected void logSkipped(ValidationContext context, ValidationRuleDefinition definition, String reason) {
        skipped = true;
        log.info("[Validation] {} rule skipped. reportCode={}, ruleId={}, reason={}",
                getType(),
                reportCode(context),
                ruleId(definition),
                reason);
    }

    protected void logPassed(ValidationContext context, ValidationRuleDefinition definition, String detail) {
        detailedPassLogged = true;
        log.info("[Validation] {} rule passed. reportCode={}, ruleId={}, detail={}",
                getType(),
                reportCode(context),
                ruleId(definition),
                detail);
    }

    protected JsonNode configuration(ValidationRuleDefinition definition) {
        return configReader.read(definition.getConfiguration());
    }

    protected void addError(
            ValidationContext context,
            ValidationResult result,
            ValidationRuleDefinition definition,
            String field) {
        ValidationError error = buildError(definition, field);
        log.info("[Validation] {} rule failed. reportCode={}, ruleId={}, field={}, errorCode={}, message={}",
                getType(),
                reportCode(context),
                ruleId(definition),
                field,
                error.code(),
                error.message());
        result.add(error);
    }

    protected ValidationError buildError(ValidationRuleDefinition definition, String field) {
        String code = definition.getErrorCode() != null && !definition.getErrorCode().isBlank()
                ? definition.getErrorCode()
                : ErrorCode.INVALID_INPUT_PARAMETER.getCode();
        String message = definition.getErrorMessage() != null && !definition.getErrorMessage().isBlank()
                ? definition.getErrorMessage()
                : "Validation failed";
        return new ValidationError(field, code, message);
    }

    protected Object value(ValidationContext context, String field) {
        return context.parameters().get(field);
    }

    protected boolean isEmpty(Object value) {
        return value == null || value.toString().isBlank();
    }

    protected boolean isPresent(Object value) {
        return !isEmpty(value);
    }

    protected String asString(Object value) {
        return value == null ? null : value.toString();
    }

    protected BigDecimal asNumber(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString().trim());
    }

    protected LocalDate asLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return instant.atZone(ZoneOffset.UTC).toLocalDate();
        }
        throw invalidTemporalType(value);
    }

    protected Instant requireInstant(Object value, String fieldName) {
        return ReportInstantParser.parseRequiredNullable(value, fieldName);
    }

    protected List<String> readFields(JsonNode config) {
        List<String> fields = new ArrayList<>();
        JsonNode fieldsNode = config.get("fields");
        if (fieldsNode != null && fieldsNode.isArray()) {
            fieldsNode.forEach(node -> fields.add(node.asText()));
        }
        return fields;
    }

    protected String requiredField(JsonNode config) {
        return requiredText(config, "field");
    }

    protected String requiredText(JsonNode config, String property) {
        String value = config.path(property).asText(null);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing configuration property: " + property);
        }
        return value;
    }

    protected boolean matchesCondition(ValidationContext context, JsonNode config) {
        String conditionField = config.path("conditionField").asText(null);
        if (conditionField == null || conditionField.isBlank()) {
            return true;
        }
        Object actual = value(context, conditionField);
        String operator = config.path("operator").asText("EQUAL").toUpperCase(Locale.ROOT);
        JsonNode expectedNode = config.get("conditionValue");
        return switch (operator) {
            case "NOT_EQUAL" -> !equalsValue(actual, expectedNode);
            case "IN" -> inList(actual, expectedNode);
            case "NOT_IN" -> !inList(actual, expectedNode);
            case "IS_NULL" -> isEmpty(actual);
            case "IS_NOT_NULL" -> isPresent(actual);
            case "GREATER_THAN" -> compareNumber(actual, expectedNode) > 0;
            case "LESS_THAN" -> compareNumber(actual, expectedNode) < 0;
            default -> equalsValue(actual, expectedNode);
        };
    }

    private boolean equalsValue(Object actual, JsonNode expectedNode) {
        if (expectedNode == null || expectedNode.isNull()) {
            return isEmpty(actual);
        }
        if (expectedNode.isBoolean()) {
            return Boolean.parseBoolean(asString(actual)) == expectedNode.asBoolean();
        }
        if (expectedNode.isNumber()) {
            BigDecimal actualNumber = asNumber(actual);
            return actualNumber != null && actualNumber.compareTo(expectedNode.decimalValue()) == 0;
        }
        return expectedNode.asText().equals(asString(actual));
    }

    private boolean inList(Object actual, JsonNode expectedNode) {
        if (expectedNode == null || !expectedNode.isArray()) {
            return false;
        }
        String actualText = asString(actual);
        for (JsonNode node : expectedNode) {
            if (node.asText().equals(actualText)) {
                return true;
            }
        }
        return false;
    }

    private int compareNumber(Object actual, JsonNode expectedNode) {
        BigDecimal left = asNumber(actual);
        BigDecimal right = expectedNode == null ? null : expectedNode.decimalValue();
        if (left == null || right == null) {
            return -1;
        }
        return left.compareTo(right);
    }

    protected long daysBetween(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    private ReportingDomainException invalidTemporalType(Object value) {
        return new ReportingDomainException(
                ErrorCode.REPORT_PARAMETER_INVALID.getCode(),
                "Date/time parameters must be java.time.Instant, actualType=" + value.getClass().getSimpleName());
    }

    private String reportCode(ValidationContext context) {
        return context.reportDefinition().getReportCode();
    }


    private Long ruleId(ValidationRuleDefinition definition) {
        return definition.getId() != null ? definition.getId().getValue() : null;
    }
}
