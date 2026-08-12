package com.sample.system.reporting.service.application.validation.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.engine.DefaultValidationEngine;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.registry.ValidationRuleRegistry;
import com.sample.system.reporting.service.application.validation.rule.impl.*;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import com.sample.system.reporting.service.domain.model.valueObject.ReportDefinitionId;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Behaviour of the parameter-only validators: a rule either passes or reports its configured error. */
class CommonValidationRulesTest {

    private final ValidationConfigReader config = new ValidationConfigReader(new ObjectMapper());
    private final DefaultValidationEngine engine = new DefaultValidationEngine(new ValidationRuleRegistry(List.of(
            new RequiredValidator(config), new MaxLengthValidator(config), new MinLengthValidator(config),
            new FixedLengthValidator(config), new RegexValidator(config), new InListValidator(config),
            new NotInListValidator(config), new AllOrNoneValidator(config), new ExactlyOneValidator(config),
            new PositiveValidator(config), new NegativeValidator(config))));

    private boolean passes(ValidationRuleType type, String configuration, Map<String, Object> params) {
        ReportDefinition definition = new ReportDefinition();
        definition.setId(new ReportDefinitionId(1L));
        ValidationRuleDefinition rule = new ValidationRuleDefinition();
        rule.setValidationRuleType(type);
        rule.setExecutionOrder(1);
        rule.setConfiguration(configuration);
        rule.setErrorCode("2");
        rule.setErrorMessage("failed " + type);
        rule.setEnabled(true);
        try {
            engine.validate(new ValidationContext(definition, new HashMap<>(params), "tester", Locale.ENGLISH),
                    List.of(rule));
            return true;
        } catch (ReportingDomainException ex) {
            assertFalse(ex.getValidationErrors().isEmpty());
            return false;
        }
    }

    @Test
    void required() {
        assertTrue(passes(ValidationRuleType.REQUIRED, "{\"field\":\"a\"}", Map.of("a", "x")));
        assertFalse(passes(ValidationRuleType.REQUIRED, "{\"field\":\"a\"}", Map.of()));
        assertFalse(passes(ValidationRuleType.REQUIRED, "{\"field\":\"a\"}", Map.of("a", " ")));
    }

    @Test
    void lengthRules() {
        assertTrue(passes(ValidationRuleType.MAX_LENGTH, "{\"field\":\"a\",\"length\":3}", Map.of("a", "abc")));
        assertFalse(passes(ValidationRuleType.MAX_LENGTH, "{\"field\":\"a\",\"length\":3}", Map.of("a", "abcd")));
        assertTrue(passes(ValidationRuleType.MIN_LENGTH, "{\"field\":\"a\",\"length\":2}", Map.of("a", "ab")));
        assertFalse(passes(ValidationRuleType.MIN_LENGTH, "{\"field\":\"a\",\"length\":2}", Map.of("a", "a")));
        assertTrue(passes(ValidationRuleType.FIXED_LENGTH, "{\"field\":\"a\",\"length\":10}", Map.of("a", "1234567890")));
        assertFalse(passes(ValidationRuleType.FIXED_LENGTH, "{\"field\":\"a\",\"length\":10}", Map.of("a", "123")));
    }

    @Test
    void emptyOptionalFieldIsSkipped() {
        assertTrue(passes(ValidationRuleType.MAX_LENGTH, "{\"field\":\"a\",\"length\":3}", Map.of()));
        assertTrue(passes(ValidationRuleType.REGEX, "{\"field\":\"a\",\"pattern\":\"\\\\d+\"}", Map.of()));
    }

    @Test
    void regex() {
        assertTrue(passes(ValidationRuleType.REGEX, "{\"field\":\"a\",\"pattern\":\"\\\\d{10}\"}", Map.of("a", "1234567890")));
        assertFalse(passes(ValidationRuleType.REGEX, "{\"field\":\"a\",\"pattern\":\"\\\\d{10}\"}", Map.of("a", "12a4567890")));
    }

    @Test
    void lists() {
        String cfg = "{\"field\":\"a\",\"values\":[\"X\",\"Y\"]}";
        assertTrue(passes(ValidationRuleType.IN_LIST, cfg, Map.of("a", "X")));
        assertFalse(passes(ValidationRuleType.IN_LIST, cfg, Map.of("a", "Z")));
        assertTrue(passes(ValidationRuleType.NOT_IN_LIST, cfg, Map.of("a", "Z")));
        assertFalse(passes(ValidationRuleType.NOT_IN_LIST, cfg, Map.of("a", "X")));
    }

    @Test
    void fieldGroups() {
        String cfg = "{\"fields\":[\"a\",\"b\"]}";
        assertTrue(passes(ValidationRuleType.ALL_OR_NONE, cfg, Map.of()));
        assertTrue(passes(ValidationRuleType.ALL_OR_NONE, cfg, Map.of("a", "1", "b", "2")));
        assertFalse(passes(ValidationRuleType.ALL_OR_NONE, cfg, Map.of("a", "1")));
        assertTrue(passes(ValidationRuleType.EXACTLY_ONE, cfg, Map.of("a", "1")));
        assertFalse(passes(ValidationRuleType.EXACTLY_ONE, cfg, Map.of("a", "1", "b", "2")));
        assertFalse(passes(ValidationRuleType.EXACTLY_ONE, cfg, Map.of()));
    }

    @Test
    void sign() {
        assertTrue(passes(ValidationRuleType.POSITIVE, "{\"field\":\"a\"}", Map.of("a", 5)));
        assertFalse(passes(ValidationRuleType.POSITIVE, "{\"field\":\"a\"}", Map.of("a", 0)));
        assertTrue(passes(ValidationRuleType.NEGATIVE, "{\"field\":\"a\"}", Map.of("a", -1)));
        assertFalse(passes(ValidationRuleType.NEGATIVE, "{\"field\":\"a\"}", Map.of("a", 1)));
    }
}
