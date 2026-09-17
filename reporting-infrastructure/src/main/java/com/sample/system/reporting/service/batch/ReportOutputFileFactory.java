package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ReportOutputFileFactory {

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final Path localStoragePath;

    public ReportOutputFileFactory(@Value("${reporting.storage.local-path:report-output}") String localStoragePath) {
        this.localStoragePath = Path.of(localStoragePath);
    }

    public ReportOutputFile create(ReportBatchContext context) {
        Long executionId = context.getReportExecution().getId().getValue();
        ReportFormat format = context.getReportFormat();
        String fileName = fileName(context, format);
        String objectName = "report-executions/" + executionId + "/" + fileName;
        Path finalFile = localStoragePath.resolve(objectName);
        try {
            Files.createDirectories(finalFile.getParent());
            Path tempFile = format == ReportFormat.XLSX
                    ? finalFile
                    : Files.createTempFile(finalFile.getParent(), fileName + ".", ".tmp");
            return new ReportOutputFile(tempFile, finalFile, fileName, objectName, format);
        } catch (IOException ex) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Cannot create report output file: " + ex.getMessage());
        }
    }

    private String fileName(ReportBatchContext context, ReportFormat format) {
        return sanitize(context.getReportDefinition().getReportCode())
                + "_"
                + FILE_TIMESTAMP.format(LocalDateTime.now())
                + "."
                + format.extension();
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "report";
        }
        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
