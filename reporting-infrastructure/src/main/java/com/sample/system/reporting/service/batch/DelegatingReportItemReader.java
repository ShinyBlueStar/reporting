package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchReader;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamReader;

@Slf4j
public class DelegatingReportItemReader implements ItemStreamReader<Object> {

    private final ReportBatchReader<Object> delegate;
    private final ReportBatchContext context;
    private long rowsRead;
    private boolean closed;

    public DelegatingReportItemReader(ReportBatchReader<Object> delegate, ReportBatchContext context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        rowsRead = 0L;
        closed = false;
        Long executionId = context.getReportExecution().getId().getValue();
        String reportCode = context.getReportDefinition().getReportCode();
        log.info("[BatchReader] Opening. executionId={}, reportCode={}, delegate={}, chunkSize={}, exportMode={}",
                executionId,
                reportCode,
                delegate.getClass().getSimpleName(),
                context.getChunkSize(),
                context.isExportMode());
        delegate.open(context);
    }

    @Override
    public Object read() {
        Object item = delegate.read();
        if (item != null) {
            rowsRead++;
            context.getSharedState().put(ReportBatchSharedStateKeys.READ_ROW_COUNT, rowsRead);
            Long executionId = context.getReportExecution().getId().getValue();
            if (rowsRead == 1L) {
                log.info("[BatchReader] First row read. executionId={}, reportCode={}, delegate={}",
                        executionId,
                        context.getReportDefinition().getReportCode(),
                        delegate.getClass().getSimpleName());
            } else if (rowsRead % 1000L == 0L) {
                log.info("[BatchReader] Read progress. executionId={}, reportCode={}, rowsRead={}",
                        executionId,
                        context.getReportDefinition().getReportCode(),
                        rowsRead);
            }
        }
        return item;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        Long executionId = context.getReportExecution().getId().getValue();
        context.getSharedState().put(ReportBatchSharedStateKeys.READ_ROW_COUNT, rowsRead);
        delegate.close();
        log.info("[BatchReader] Closed. executionId={}, reportCode={}, delegate={}, totalRowsRead={}",
                executionId,
                context.getReportDefinition().getReportCode(),
                delegate.getClass().getSimpleName(),
                rowsRead);
    }
}
