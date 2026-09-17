package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.domain.model.enums.ReportFormat;

import java.nio.file.Path;

public class ReportOutputFile {

    private final Path tempFile;
    private final Path finalFile;
    private final String fileName;
    private final String objectName;
    private final ReportFormat format;

    public ReportOutputFile(Path tempFile,
                            Path finalFile,
                            String fileName,
                            String objectName,
                            ReportFormat format) {
        this.tempFile = tempFile;
        this.finalFile = finalFile;
        this.fileName = fileName;
        this.objectName = objectName;
        this.format = format;
    }

    public Path getTempFile() {
        return tempFile;
    }

    public Path getFinalFile() {
        return finalFile;
    }

    public String getFileName() {
        return fileName;
    }

    public String getObjectName() {
        return objectName;
    }

    public ReportFormat getFormat() {
        return format;
    }
}
