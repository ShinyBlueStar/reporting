package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchProcessor;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@Slf4j
public class DelegatingReportItemProcessor implements ItemProcessor<Object, Object> {

    private final ReportBatchProcessor<Object, Object> delegate;
    private final ReportBatchContext context;
    private long itemsProcessed;

    public DelegatingReportItemProcessor(ReportBatchProcessor<Object, Object> delegate, ReportBatchContext context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public Object process(Object item) {
        Object result = delegate.process(item, context);
        itemsProcessed++;
        Long executionId = context.getReportExecution().getId().getValue();
        String reportCode = context.getReportDefinition().getReportCode();
        if (itemsProcessed == 1L) {
            log.info("[BatchProcessor] First row processed. executionId={}, reportCode={}, delegate={}, exportMode={}",
                    executionId,
                    reportCode,
                    delegate.getClass().getSimpleName(),
                    context.isExportMode());
        } else if (itemsProcessed % 1000L == 0L) {
            log.info("[BatchProcessor] Process progress. executionId={}, reportCode={}, delegate={}, rowsProcessed={}",
                    executionId,
                    reportCode,
                    delegate.getClass().getSimpleName(),
                    itemsProcessed);
        }
        return result;
    }
}
