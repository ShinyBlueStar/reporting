package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.output.IReportDefinitionRepository;
import com.sample.system.reporting.service.application.ports.output.IValidationRuleDefinitionRepository;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ValidationRuleDefinition;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValidationRuleDefinitionServiceImplTest {

    private final IValidationRuleDefinitionRepository repository = mock(IValidationRuleDefinitionRepository.class);
    private final IReportDefinitionRepository definitions = mock(IReportDefinitionRepository.class);
    private final ValidationRuleDefinitionServiceImpl service =
            new ValidationRuleDefinitionServiceImpl(repository, definitions);

    private ValidationRuleDefinition rule() {
        ValidationRuleDefinition r = new ValidationRuleDefinition();
        r.setReportDefinitionId(10L);
        r.setValidationRuleType(ValidationRuleType.REQUIRED);
        r.setConfiguration("{\"field\":\"a\"}");
        r.setErrorCode("2");
        r.setErrorMessage("a is required");
        return r;
    }

    private void assertCode(ErrorCode expected, Runnable action) {
        ReportingDomainException ex = assertThrows(ReportingDomainException.class, action::run);
        assertEquals(expected.getCode(), ex.getErrorCode());
    }

    @Test
    void createRejectsIncompleteRule() {
        ValidationRuleDefinition r = rule();
        r.setErrorMessage(" ");
        assertCode(ErrorCode.INVALID_INPUT_PARAMETER, () -> service.create(r));
    }

    @Test
    void createRequiresExistingReportDefinition() {
        when(definitions.findById(10L)).thenReturn(null);
        assertCode(ErrorCode.REPORT_DEFINITION_NOT_FOUND, () -> service.create(rule()));
        verify(repository, never()).save(any());
    }

    @Test
    void createSavesValidRule() {
        when(definitions.findById(10L)).thenReturn(new ReportDefinition());
        ValidationRuleDefinition r = rule();
        when(repository.save(r)).thenReturn(r);
        assertSame(r, service.create(r));
    }

    @Test
    void updateWithoutIdFailsAndUnknownIdIsNotFound() {
        assertCode(ErrorCode.INVALID_INPUT_PARAMETER, () -> service.update(rule()));
        assertCode(ErrorCode.VALIDATION_RULE_NOT_FOUND, () -> service.findById(5L));
    }
}
