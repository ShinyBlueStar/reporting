package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchWriter;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamWriter;

@Slf4j
public class DelegatingReportItemWriter implements ItemStreamWriter<Object> {

    private final ReportBatchWriter<Object> delegate;
    private final ReportBatchContext context;
    private long totalWritten;
    private boolean closed;

    public DelegatingReportItemWriter(ReportBatchWriter<Object> delegate, ReportBatchContext context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public void write(Chunk<? extends Object> chunk) {
        int chunkSize = chunk.size();
        if (chunkSize == 0) {
            return;
        }
        delegate.write(chunk.getItems(), context);
        long previousTotal = totalWritten;
        totalWritten += chunkSize;
        context.getSharedState().put(ReportBatchSharedStateKeys.WRITTEN_ROW_COUNT, totalWritten);
        Long executionId = context.getReportExecution().getId().getValue();
        String reportCode = context.getReportDefinition().getReportCode();
        if (previousTotal == 0L) {
            log.info("[BatchWriter] First chunk written. executionId={}, reportCode={}, delegate={}, format={}, chunkSize={}",
                    executionId,
                    reportCode,
                    delegate.getClass().getSimpleName(),
                    context.getReportFormat(),
                    chunkSize);
        } else if (totalWritten / 1000L > previousTotal / 1000L) {
            log.info("[BatchWriter] Write progress. executionId={}, reportCode={}, delegate={}, format={}, totalRowsWritten={}",
                    executionId,
                    reportCode,
                    delegate.getClass().getSimpleName(),
                    context.getReportFormat(),
                    totalWritten);
        }
    }

    @Override
    public void open(ExecutionContext executionContext) {
        totalWritten = 0L;
        closed = false;
        Long executionId = context.getReportExecution().getId().getValue();
        log.info("[BatchWriter] Opening. executionId={}, reportCode={}, delegate={}, format={}, exportMode={}",
                executionId,
                context.getReportDefinition().getReportCode(),
                delegate.getClass().getSimpleName(),
                context.getReportFormat(),
                context.isExportMode());
        delegate.open(context);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        context.getSharedState().put(ReportBatchSharedStateKeys.WRITTEN_ROW_COUNT, totalWritten);
        delegate.close(context);
        log.info("[BatchWriter] Closed. executionId={}, reportCode={}, delegate={}, format={}, totalRowsWritten={}",
                context.getReportExecution().getId().getValue(),
                context.getReportDefinition().getReportCode(),
                delegate.getClass().getSimpleName(),
                context.getReportFormat(),
                totalWritten);
    }
}
