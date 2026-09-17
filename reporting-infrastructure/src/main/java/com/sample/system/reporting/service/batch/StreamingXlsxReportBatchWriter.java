package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import com.sample.system.reporting.service.reportformat.ReportTemplateColumnResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class StreamingXlsxReportBatchWriter extends AbstractStreamingReportBatchWriter<XlsxStreamingReportResource> {

    private final ReportTemplateColumnResolver columnResolver;

    public StreamingXlsxReportBatchWriter(ReportOutputFileFactory outputFileFactory,
                                          ReportTemplateColumnResolver columnResolver) {
        super(outputFileFactory, ReportFormat.XLSX);
        this.columnResolver = columnResolver;
    }

    @Override
    protected XlsxStreamingReportResource createResource(ReportOutputFile outputFile, ReportBatchContext context) {
        return new XlsxStreamingReportResource(
                outputFile,
                columnResolver,
                StreamingReportBatchWriterSupport.template(context));
    }

    @Override
    protected void writeItems(XlsxStreamingReportResource resource, List<? extends Map<String, Object>> items)
            throws IOException {
        resource.writeRows(items);
    }

    @Override
    protected Class<XlsxStreamingReportResource> resourceType() {
        return XlsxStreamingReportResource.class;
    }
}
