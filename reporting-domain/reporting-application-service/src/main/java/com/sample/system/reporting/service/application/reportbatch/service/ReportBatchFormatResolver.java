package com.sample.system.reporting.service.application.reportbatch.service;

import com.sample.system.reporting.service.application.command.reportexecution.ExecuteReportCommand;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
public class ReportBatchFormatResolver {

    private static final Set<ReportFormat> SUPPORTED_EXPORT_FORMATS =
            EnumSet.of(ReportFormat.JSON, ReportFormat.CSV, ReportFormat.XLSX);

    public ReportFormat resolve(ExecuteReportCommand command, ReportDefinition definition) {
        String requestedFormat = command.getReportFormat();
        String definitionFormat = definition.getReportType();
        try {
            ReportFormat format = ReportFormat.from(
                    requestedFormat == null || requestedFormat.isBlank() ? definitionFormat : requestedFormat);
            validateAllowedFormat(definition, format);
            return format;
        } catch (IllegalArgumentException ex) {
            throw new ReportingDomainException(ErrorCode.INVALID_INPUT_PARAMETER.getCode(), ex.getMessage());
        }
    }

    public boolean isExportRequested(ExecuteReportCommand command) {
        return command.getReportFormat() != null && !command.getReportFormat().isBlank();
    }

    private void validateAllowedFormat(ReportDefinition definition, ReportFormat format) {
        if (!SUPPORTED_EXPORT_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Report format is not implemented yet: " + format.name());
        }
    }
}
