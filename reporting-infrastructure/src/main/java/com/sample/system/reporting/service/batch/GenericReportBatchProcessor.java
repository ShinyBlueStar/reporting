package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchProcessor;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class GenericReportBatchProcessor implements ReportBatchProcessor<Map<String, Object>, Map<String, Object>> {

    private static final long DEFAULT_MAX_EXPORT_ROWS = 20_000L;

    @Override
    public boolean supports(ReportDefinition reportDefinition) {
        return reportDefinition.getSqlQuery() != null && !reportDefinition.getSqlQuery().isBlank();
    }

    @Override
    public Map<String, Object> process(Map<String, Object> item, ReportBatchContext context) {
        Number processedCount = (Number) context.getSharedState().compute(
                ReportBatchSharedStateKeys.PROCESSED_ROW_COUNT,
                (key, current) -> current == null ? 1L : ((Number) current).longValue() + 1);

        if (context.isExportMode()) {
            enforceExportLimit(processedCount.longValue(), context.getReportDefinition());
        }
        if (processedCount.longValue() == 1L) {
            log.info("[BatchProcessor] First row processed. executionId={}, reportCode={}, processedCount={}, exportMode={}",
                    context.getReportExecution().getId().getValue(),
                    context.getReportDefinition().getReportCode(),
                    processedCount.longValue(),
                    context.isExportMode());
        } else if (processedCount.longValue() % 1000L == 0L) {
            log.info("[BatchProcessor] Process progress. executionId={}, reportCode={}, processedCount={}, exportMode={}",
                    context.getReportExecution().getId().getValue(),
                    context.getReportDefinition().getReportCode(),
                    processedCount.longValue(),
                    context.isExportMode());
        }
        return item;
    }

    private void enforceExportLimit(long processedCount, ReportDefinition definition) {
        long limit = definition.getMaxExportRows() == null || definition.getMaxExportRows() <= 0
                ? DEFAULT_MAX_EXPORT_ROWS
                : definition.getMaxExportRows();
        if (processedCount > limit) {
            log.warn("Export row limit exceeded. reportCode={}, processedCount={}, maxExportRows={}",
                    definition.getReportCode(),
                    processedCount,
                    limit);
            throw new ReportingDomainException(
                    ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                    "Export row limit exceeded. maxExportRows=" + limit);
        }
    }
}
