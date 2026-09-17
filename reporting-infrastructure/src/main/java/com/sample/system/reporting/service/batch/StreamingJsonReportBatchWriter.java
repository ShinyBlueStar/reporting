package com.sample.system.reporting.service.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Component
public class StreamingJsonReportBatchWriter extends AbstractStreamingReportBatchWriter<JsonStreamingReportResource> {

    private final ObjectMapper objectMapper;

    public StreamingJsonReportBatchWriter(ReportOutputFileFactory outputFileFactory, ObjectMapper objectMapper) {
        super(outputFileFactory, ReportFormat.JSON);
        this.objectMapper = objectMapper;
    }

    @Override
    protected JsonStreamingReportResource createResource(ReportOutputFile outputFile, ReportBatchContext context)
            throws IOException {
        JsonStreamingReportResource resource = new JsonStreamingReportResource(
                outputFile,
                objectMapper.getFactory().createGenerator(
                        Files.newBufferedWriter(outputFile.getTempFile(), StandardCharsets.UTF_8)),
                objectMapper);
        resource.start(context);
        return resource;
    }

    @Override
    protected void writeItems(JsonStreamingReportResource resource, List<? extends Map<String, Object>> items)
            throws IOException {
        resource.writeRows(items);
    }

    @Override
    protected Class<JsonStreamingReportResource> resourceType() {
        return JsonStreamingReportResource.class;
    }
}
