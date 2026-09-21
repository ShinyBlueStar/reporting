package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.ReportFileStoragePort;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConditionalOnProperty(name = "reporting.storage.type", havingValue = "local")
public class LocalReportFileStorageAdapter implements ReportFileStoragePort {

    private final Path localStoragePath;

    public LocalReportFileStorageAdapter(
            @Value("${reporting.storage.local-path:report-output}") String localStoragePath) {
        this.localStoragePath = Path.of(localStoragePath).toAbsolutePath().normalize();
    }

    @Override
    public Resource readContent(ReportFile reportFile) {
        if (reportFile.getObjectName() == null || reportFile.getObjectName().isBlank()) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Report file object name is missing");
        }
        Path target = localStoragePath.resolve(reportFile.getObjectName()).normalize();
        if (!target.startsWith(localStoragePath)) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Report file path is outside the storage directory");
        }
        FileSystemResource resource = new FileSystemResource(target);
        if (!resource.exists() || !resource.isReadable()) {
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Cannot read generated report file");
        }
        return resource;
    }

    @Override
    public void store(String objectName, Path stagedFile, String contentType) {
        // The batch writer already produced the file inside the storage directory.
    }

    @Override
    public String providerName() {
        return "LOCAL";
    }

    @Override
    public String bucketName() {
        return localStoragePath.getFileName() != null ? localStoragePath.getFileName().toString() : "local";
    }
}
