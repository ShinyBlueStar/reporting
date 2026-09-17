package com.sample.system.reporting.service.reportformat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@AllArgsConstructor
public class ReportFormatResult {

    private final Path file;
    private final String fileName;
    private final String fileExtension;
    private final String contentType;
    private final long fileSize;
    private final String objectName;

    public long fileSize() {
        return fileSize;
    }
}
