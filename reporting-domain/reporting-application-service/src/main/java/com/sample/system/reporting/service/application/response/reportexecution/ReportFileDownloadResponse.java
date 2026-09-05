package com.sample.system.reporting.service.application.response.reportexecution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class ReportFileDownloadResponse {

    private final Resource content;
    private final String fileName;
    private final String contentType;
}
