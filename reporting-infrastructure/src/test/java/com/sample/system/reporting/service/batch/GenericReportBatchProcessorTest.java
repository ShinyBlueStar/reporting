package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import com.sample.system.reporting.service.domain.model.valueObject.ReportExecutionId;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class GenericReportBatchProcessorTest {

    private final GenericReportBatchProcessor processor = new GenericReportBatchProcessor();

    private ReportBatchContext context(Long maxExportRows, boolean exportMode) {
        ReportDefinition definition = new ReportDefinition();
        definition.setReportCode("R");
        definition.setSqlQuery("select 1 from dual");
        definition.setMaxExportRows(maxExportRows);
        ReportExecution execution = new ReportExecution();
        execution.setId(new ReportExecutionId(1L));
        return new ReportBatchContext(definition, execution, ReportFormat.CSV, Map.of(), 100, exportMode,
                new ConcurrentHashMap<>());
    }

    @Test
    void supportsOnlyDefinitionsWithSql() {
        ReportDefinition withSql = new ReportDefinition();
        withSql.setSqlQuery("select 1");
        assertTrue(processor.supports(withSql));
        assertFalse(processor.supports(new ReportDefinition()));
    }

    @Test
    void passesRowThroughAndCountsProcessedRows() {
        ReportBatchContext ctx = context(10L, true);
        Map<String, Object> row = Map.of("a", 1);

        assertSame(row, processor.process(row, ctx));
        processor.process(row, ctx);

        assertEquals(2L, ((Number) ctx.getSharedState().get(ReportBatchSharedStateKeys.PROCESSED_ROW_COUNT)).longValue());
    }

    @Test
    void exportAboveLimitFails() {
        ReportBatchContext ctx = context(2L, true);
        Map<String, Object> row = Map.of("a", 1);
        processor.process(row, ctx);
        processor.process(row, ctx);

        assertThrows(ReportingDomainException.class, () -> processor.process(row, ctx));
    }

    @Test
    void defaultLimitIsTwentyThousandAndNotEnforcedOutsideExportMode() {
        ReportBatchContext exportCtx = context(null, true);
        ReportBatchContext previewCtx = context(1L, false);
        Map<String, Object> row = Map.of("a", 1);

        for (int i = 0; i < 20_000; i++) {
            processor.process(row, exportCtx);
        }
        assertThrows(ReportingDomainException.class, () -> processor.process(row, exportCtx));

        processor.process(row, previewCtx);
        assertDoesNotThrow(() -> processor.process(row, previewCtx));
    }
}
