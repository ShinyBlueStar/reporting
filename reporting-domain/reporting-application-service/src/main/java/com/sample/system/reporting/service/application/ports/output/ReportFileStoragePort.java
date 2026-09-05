package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.domain.model.entity.ReportFile;
import org.springframework.core.io.Resource;

import java.nio.file.Path;

public interface ReportFileStoragePort {

    Resource readContent(ReportFile reportFile);

    /**
     * Persists a generated report. Reports are first written to a local staging file; implementations that
     * keep the file elsewhere (object storage) upload it and remove the staged copy.
     */
    void store(String objectName, Path stagedFile, String contentType);

    /** Value recorded in {@code report_file.storage_provider}, e.g. {@code S3} or {@code LOCAL}. */
    String providerName();

    /** Bucket (or logical container) the objects live in. */
    String bucketName();
}
