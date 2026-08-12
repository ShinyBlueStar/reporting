package com.sample.system.reporting.service.application.validation.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.validation.config.ValidationConfigReader;
import com.sample.system.reporting.service.application.validation.model.ValidationContext;
import com.sample.system.reporting.service.application.validation.registry.ValidationRuleRegistry;
import com.sample.system.reporting.service.application.validation.rule.impl.AtLeastOneRequiredValidator;
import com.sample.system.reporting.service.application.validation.rule.impl.DateRangeValidator;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import com.sample.system.reporting.service.domain.model.valueObject.ReportDefinitionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultValidationEngineTest {

    private DefaultValidationEngine validationEngine;

    @BeforeEach
    void setUp() {
        ValidationConfigReader configReader = new ValidationConfigReader(new ObjectMapper());
        ValidationRuleRegistry registry = new ValidationRuleRegistry(List.of(
                new AtLeastOneRequiredValidator(configReader),
                new DateRangeValidator(configReader)));
        validationEngine = new DefaultValidationEngine(registry);
    }

    @Test
    void collectsAllValidationErrorsBeforeThrowing() {
        ReportDefinition definition = new ReportDefinition();
        definition.setId(new ReportDefinitionId(10L));
        definition.setReportCode("LoanPortfolioReportService");

        ValidationRuleDefinition atLeastOneRule = rule(
                ValidationRuleType.AT_LEAST_ONE_REQUIRED,
                1,
                "{\"fields\":[\"fileNumber\",\"fromDate\"]}",
                "At least one search parameter is required");
        ValidationRuleDefinition dateRangeRule = rule(
                ValidationRuleType.DATE_RANGE,
                2,
                "{\"fromField\":\"fromDate\",\"toField\":\"toDate\"}",
                "fromDate must not be greater than toDate");

        ReportingDomainException exception = assertThrows(
                ReportingDomainException.class,
                () -> validationEngine.validate(
                        new ValidationContext(
                                definition,
                                Map.of(
                                        "fromDate", Instant.parse("2026-01-02T00:00:00Z"),
                                        "toDate", Instant.parse("2026-01-01T00:00:00Z")),
                                "tester",
                                Locale.ENGLISH),
                        List.of(atLeastOneRule, dateRangeRule)));

        assertEquals(1, exception.getValidationErrors().size());
        assertTrue(exception.getMessage().contains("fromDate must not be greater than toDate"));
    }

    @Test
    void passesWhenConfiguredRulesAreSatisfied() {
        ReportDefinition definition = new ReportDefinition();
        definition.setId(new ReportDefinitionId(10L));

        ValidationRuleDefinition atLeastOneRule = rule(
                ValidationRuleType.AT_LEAST_ONE_REQUIRED,
                1,
                "{\"fields\":[\"fileNumber\",\"fromDate\"]}",
                "At least one search parameter is required");

        assertDoesNotThrow(() -> validationEngine.validate(
                new ValidationContext(
                        definition,
                        Map.of("fileNumber", "123"),
                        "tester",
                        Locale.ENGLISH),
                List.of(atLeastOneRule)));
    }

    private ValidationRuleDefinition rule(
            ValidationRuleType type,
            int order,
            String configuration,
            String message) {
        ValidationRuleDefinition rule = new ValidationRuleDefinition();
        rule.setValidationRuleType(type);
        rule.setExecutionOrder(order);
        rule.setConfiguration(configuration);
        rule.setErrorCode("2");
        rule.setErrorMessage(message);
        rule.setEnabled(true);
        return rule;
    }
}
