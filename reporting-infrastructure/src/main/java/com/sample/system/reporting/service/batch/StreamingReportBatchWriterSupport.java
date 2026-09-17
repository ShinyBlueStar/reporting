package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;

import java.io.IOException;

final class StreamingReportBatchWriterSupport {

    private StreamingReportBatchWriterSupport() {
    }

    static boolean supportsSqlReport(ReportDefinition reportDefinition,
                                     ReportFormat expectedFormat,
                                     ReportFormat actualFormat) {
        return expectedFormat == actualFormat
                && reportDefinition.getSqlQuery() != null
                && !reportDefinition.getSqlQuery().isBlank();
    }

    static Long executionId(ReportBatchContext context) {
        return context.getReportExecution().getId() != null
                ? context.getReportExecution().getId().getValue()
                : null;
    }

    static String reportCode(ReportBatchContext context) {
        return context.getReportDefinition().getReportCode();
    }

    static ReportTemplate template(ReportBatchContext context) {
        return (ReportTemplate) context.getSharedState().get(ReportBatchSharedStateKeys.REPORT_TEMPLATE);
    }

    static ReportingDomainException streamUnavailable(ReportFormat format) {
        return new ReportingDomainException(
                ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                format.name() + " report stream is not available");
    }

    static ReportingDomainException openFailure(ReportFormat format, IOException ex) {
        return ioFailure("open " + format.name(), ex);
    }

    static ReportingDomainException writeFailure(ReportFormat format, IOException ex) {
        return ioFailure("stream " + format.name() + " report rows", ex);
    }

    private static ReportingDomainException ioFailure(String action, IOException ex) {
        return new ReportingDomainException(
                ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                "Cannot " + action + ": " + ex.getMessage());
    }
}
