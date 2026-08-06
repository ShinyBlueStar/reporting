package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.output.IReportDefinitionRepository;
import com.sample.system.reporting.service.application.ports.output.IReportTemplateRepository;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import com.sample.system.reporting.service.domain.model.valueObject.ReportTemplateId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportTemplateServiceImplTest {

    private final IReportTemplateRepository repository = mock(IReportTemplateRepository.class);
    private final IReportDefinitionRepository definitions = mock(IReportDefinitionRepository.class);
    private final ReportTemplateServiceImpl service = new ReportTemplateServiceImpl(repository, definitions);

    private ReportTemplate template(Long id, String name) {
        ReportTemplate t = new ReportTemplate();
        if (id != null) {
            t.setId(new ReportTemplateId(id));
        }
        t.setReportDefinitionId(10L);
        t.setTemplateName(name);
        t.setSheetName("Sheet1");
        t.setColumnsJson("[{\"field\":\"id\"}]");
        return t;
    }

    private void assertCode(ErrorCode expected, Runnable action) {
        ReportingDomainException ex = assertThrows(ReportingDomainException.class, action::run);
        assertEquals(expected.getCode(), ex.getErrorCode());
    }

    @Test
    void createRejectsMissingRequiredFields() {
        ReportTemplate t = template(null, "t");
        t.setColumnsJson(" ");
        assertCode(ErrorCode.INVALID_INPUT_PARAMETER, () -> service.create(t));
    }

    @Test
    void createRequiresExistingDefinitionAndUniqueName() {
        when(definitions.findById(10L)).thenReturn(null);
        assertCode(ErrorCode.REPORT_DEFINITION_NOT_FOUND, () -> service.create(template(null, "t")));

        when(definitions.findById(10L)).thenReturn(new ReportDefinition());
        when(repository.findByReportDefinitionIdAndTemplateName(10L, "t")).thenReturn(template(1L, "t"));
        assertCode(ErrorCode.REPORT_TEMPLATE_DUPLICATE, () -> service.create(template(null, "t")));
        verify(repository, never()).save(any());
    }

    @Test
    void updateRequiresIdAndExistingTemplate() {
        assertCode(ErrorCode.INVALID_INPUT_PARAMETER, () -> service.update(template(null, "t")));
        when(repository.findById(5L)).thenReturn(null);
        assertCode(ErrorCode.REPORT_TEMPLATE_NOT_FOUND, () -> service.update(template(5L, "t")));
    }

    @Test
    void deleteRemovesExistingTemplate() {
        when(repository.findById(3L)).thenReturn(template(3L, "t"));
        service.delete(3L);
        verify(repository).deleteById(3L);
    }
}
