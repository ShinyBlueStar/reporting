package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import com.sample.system.reporting.service.reportformat.ReportTemplateColumnResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Component
public class StreamingCsvReportBatchWriter extends AbstractStreamingReportBatchWriter<CsvStreamingReportResource> {

    private final ReportTemplateColumnResolver columnResolver;

    public StreamingCsvReportBatchWriter(ReportOutputFileFactory outputFileFactory,
                                         ReportTemplateColumnResolver columnResolver) {
        super(outputFileFactory, ReportFormat.CSV);
        this.columnResolver = columnResolver;
    }

    @Override
    protected CsvStreamingReportResource createResource(ReportOutputFile outputFile, ReportBatchContext context)
            throws IOException {
        return new CsvStreamingReportResource(
                outputFile,
                Files.newBufferedWriter(outputFile.getTempFile(), StandardCharsets.UTF_8),
                columnResolver,
                StreamingReportBatchWriterSupport.template(context));
    }

    @Override
    protected void writeItems(CsvStreamingReportResource resource, List<? extends Map<String, Object>> items)
            throws IOException {
        resource.writeRows(items);
    }

    @Override
    protected Class<CsvStreamingReportResource> resourceType() {
        return CsvStreamingReportResource.class;
    }
}
