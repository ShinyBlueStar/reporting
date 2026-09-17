package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchWriter;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import com.sample.system.reporting.service.reportformat.ReportFormatResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
abstract class AbstractStreamingReportBatchWriter<R extends StreamingReportResource>
        implements ReportBatchWriter<Map<String, Object>> {

    private final ReportOutputFileFactory outputFileFactory;
    private final ReportFormat format;

    protected AbstractStreamingReportBatchWriter(ReportOutputFileFactory outputFileFactory, ReportFormat format) {
        this.outputFileFactory = outputFileFactory;
        this.format = format;
    }

    @Override
    public final boolean supports(ReportDefinition reportDefinition, ReportFormat reportFormat) {
        return StreamingReportBatchWriterSupport.supportsSqlReport(reportDefinition, format, reportFormat);
    }

    @Override
    public final void open(ReportBatchContext context) {
        try {
            ReportOutputFile outputFile = outputFileFactory.create(context);
            R resource = createResource(outputFile, context);
            context.getSharedState().put(ReportBatchSharedStateKeys.STREAMING_RESOURCE, resource);
            log.info("[BatchWriter] Opened {} stream. executionId={}, reportCode={}, tempFile={}, finalFile={}, templatePresent={}",
                    format,
                    StreamingReportBatchWriterSupport.executionId(context),
                    StreamingReportBatchWriterSupport.reportCode(context),
                    outputFile.getTempFile(),
                    outputFile.getFinalFile(),
                    StreamingReportBatchWriterSupport.template(context) != null);
        } catch (IOException ex) {
            throw StreamingReportBatchWriterSupport.openFailure(format, ex);
        }
    }

    @Override
    public final void write(List<? extends Map<String, Object>> items, ReportBatchContext context) {
        R resource = requireResource(context);
        applyTemplateIfSupported(resource, context);
        try {
            writeItems(resource, items);
        } catch (IOException ex) {
            throw StreamingReportBatchWriterSupport.writeFailure(format, ex);
        }
    }

    @Override
    public final void close(ReportBatchContext context) {
        if (context.getSharedState().get(ReportBatchSharedStateKeys.STREAMING_RESULT) != null) {
            log.info("[BatchWriter] Close skipped; {} stream already finalized. executionId={}, reportCode={}",
                    format,
                    StreamingReportBatchWriterSupport.executionId(context),
                    StreamingReportBatchWriterSupport.reportCode(context));
            return;
        }
        R resource = requireResource(context);
        applyTemplateIfSupported(resource, context);
        ReportFormatResult result = resource.closeAndGetResult();
        context.getSharedState().put(ReportBatchSharedStateKeys.STREAMING_RESULT, result);
        log.info("[BatchWriter] Finalized {} stream. executionId={}, reportCode={}, fileName={}, fileSize={}, objectName={}",
                format,
                StreamingReportBatchWriterSupport.executionId(context),
                StreamingReportBatchWriterSupport.reportCode(context),
                result.getFileName(),
                result.getFileSize(),
                result.getObjectName());
    }

    protected abstract R createResource(ReportOutputFile outputFile, ReportBatchContext context) throws IOException;

    protected abstract void writeItems(R resource, List<? extends Map<String, Object>> items) throws IOException;

    protected abstract Class<R> resourceType();

    private R requireResource(ReportBatchContext context) {
        Object resource = context.getSharedState().get(ReportBatchSharedStateKeys.STREAMING_RESOURCE);
        if (resourceType().isInstance(resource)) {
            return resourceType().cast(resource);
        }
        throw StreamingReportBatchWriterSupport.streamUnavailable(format);
    }

    private void applyTemplateIfSupported(R resource, ReportBatchContext context) {
        if (resource instanceof ReportTemplateAwareStreamingResource templateAware) {
            templateAware.applyTemplate(StreamingReportBatchWriterSupport.template(context));
        }
    }
}
