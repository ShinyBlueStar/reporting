package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.output.IReportDefinitionRepository;
import com.sample.system.reporting.service.application.ports.output.IReportParameterRepository;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportParameter;
import com.sample.system.reporting.service.domain.model.valueObject.ReportParameterId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportParameterServiceImplTest {

    private final IReportParameterRepository repository = mock(IReportParameterRepository.class);
    private final IReportDefinitionRepository definitions = mock(IReportDefinitionRepository.class);
    private final ReportParameterServiceImpl service = new ReportParameterServiceImpl(repository, definitions);

    private ReportParameter parameter(Long id, String name) {
        ReportParameter p = new ReportParameter();
        if (id != null) {
            p.setId(new ReportParameterId(id));
        }
        p.setReportDefinitionId(10L);
        p.setParameterName(name);
        p.setParameterLabel("label");
        p.setParameterType("STRING");
        return p;
    }

    private void assertCode(ErrorCode expected, Runnable action) {
        ReportingDomainException ex = assertThrows(ReportingDomainException.class, action::run);
        assertEquals(expected.getCode(), ex.getErrorCode());
    }

    @Test
    void createRequiresExistingReportDefinition() {
        when(definitions.findById(10L)).thenReturn(null);
        assertCode(ErrorCode.REPORT_DEFINITION_NOT_FOUND, () -> service.create(parameter(null, "from")));
        verify(repository, never()).save(any());
    }

    @Test
    void createRejectsDuplicateNameWithinSameReport() {
        when(definitions.findById(10L)).thenReturn(new ReportDefinition());
        when(repository.findByReportDefinitionIdAndParameterName(10L, "from")).thenReturn(parameter(1L, "from"));
        assertCode(ErrorCode.REPORT_PARAMETER_DUPLICATE, () -> service.create(parameter(null, "from")));
    }

    @Test
    void createRejectsInvalidLengthRange() {
        ReportParameter p = parameter(null, "from");
        p.setMinLength(10);
        p.setMaxLength(2);
        assertCode(ErrorCode.REPORT_PARAMETER_INVALID, () -> service.create(p));
    }

    @Test
    void createSavesValidParameter() {
        when(definitions.findById(10L)).thenReturn(new ReportDefinition());
        ReportParameter p = parameter(null, "from");
        when(repository.save(p)).thenReturn(p);
        assertSame(p, service.create(p));
    }

    @Test
    void updateAllowsKeepingOwnNameButRejectsAnotherParametersName() {
        when(definitions.findById(10L)).thenReturn(new ReportDefinition());
        when(repository.findById(1L)).thenReturn(parameter(1L, "from"));
        when(repository.findByReportDefinitionIdAndParameterName(10L, "from")).thenReturn(parameter(1L, "from"));
        when(repository.findByReportDefinitionIdAndParameterName(10L, "to")).thenReturn(parameter(2L, "to"));
        ReportParameter same = parameter(1L, "from");
        when(repository.save(same)).thenReturn(same);

        assertSame(same, service.update(same));
        assertCode(ErrorCode.REPORT_PARAMETER_DUPLICATE, () -> service.update(parameter(1L, "to")));
    }

    @Test
    void deleteUnknownParameterFails() {
        when(repository.findById(99L)).thenReturn(null);
        assertCode(ErrorCode.REPORT_PARAMETER_NOT_FOUND, () -> service.delete(99L));
        verify(repository, never()).deleteById(any());
    }
}
