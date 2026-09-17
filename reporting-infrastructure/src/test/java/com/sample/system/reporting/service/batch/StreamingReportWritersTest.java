package com.sample.system.reporting.service.batch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchSharedStateKeys;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.entity.ReportExecution;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import com.sample.system.reporting.service.domain.model.valueObject.ReportExecutionId;
import com.sample.system.reporting.service.reportformat.ReportFormatResult;
import com.sample.system.reporting.service.reportformat.ReportTemplateColumnResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class StreamingReportWritersTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path storage;

    private ReportBatchContext context(ReportFormat format) {
        ReportDefinition definition = new ReportDefinition();
        definition.setReportCode("Loan Report/1");
        definition.setSqlQuery("select 1 from dual");
        ReportExecution execution = new ReportExecution();
        execution.setId(new ReportExecutionId(42L));
        return new ReportBatchContext(definition, execution, format, Map.of("from", "2026-01-01"), 100, true,
                new ConcurrentHashMap<>());
    }

    private Map<String, Object> row(Object id, Object name) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("ID", id);
        row.put("NAME", name);
        return row;
    }

    private ReportFormatResult run(StreamingCsvReportBatchWriter writer, ReportBatchContext ctx, List<Map<String, Object>> rows) {
        writer.open(ctx);
        writer.write(rows, ctx);
        writer.close(ctx);
        return (ReportFormatResult) ctx.getSharedState().get(ReportBatchSharedStateKeys.STREAMING_RESULT);
    }

    @Test
    void csvWriterWritesHeaderEscapesQuotesAndSanitizesObjectName() throws Exception {
        StreamingCsvReportBatchWriter writer = new StreamingCsvReportBatchWriter(
                new ReportOutputFileFactory(storage.toString()), new ReportTemplateColumnResolver(objectMapper));
        ReportBatchContext ctx = context(ReportFormat.CSV);

        ReportFormatResult result = run(writer, ctx, List.of(row(1, "plain"), row(2, "say \"hi\", ok")));

        List<String> lines = Files.readAllLines(result.getFile());
        assertEquals("\"ID\",\"NAME\"", lines.get(0));
        assertEquals("\"1\",\"plain\"", lines.get(1));
        assertEquals("\"2\",\"say \"\"hi\"\", ok\"", lines.get(2));
        assertTrue(result.getObjectName().startsWith("report-executions/42/Loan_Report_1_"));
        assertTrue(result.getFileName().endsWith(".csv"));
        assertEquals(Files.size(result.getFile()), result.getFileSize());
        assertTrue(result.getFile().startsWith(storage));
    }

    @Test
    void csvWriterWithoutRowsStillProducesAFile() {
        StreamingCsvReportBatchWriter writer = new StreamingCsvReportBatchWriter(
                new ReportOutputFileFactory(storage.toString()), new ReportTemplateColumnResolver(objectMapper));
        ReportFormatResult result = run(writer, context(ReportFormat.CSV), List.of());
        assertTrue(Files.exists(result.getFile()));
    }

    @Test
    void jsonWriterProducesWellFormedDocumentWithMetadataAndRows() throws Exception {
        StreamingJsonReportBatchWriter writer = new StreamingJsonReportBatchWriter(
                new ReportOutputFileFactory(storage.toString()), objectMapper);
        ReportBatchContext ctx = context(ReportFormat.JSON);

        writer.open(ctx);
        writer.write(List.of(row(1, "a"), row(2, null)), ctx);
        writer.close(ctx);
        ReportFormatResult result = (ReportFormatResult) ctx.getSharedState().get(ReportBatchSharedStateKeys.STREAMING_RESULT);

        JsonNode json = objectMapper.readTree(result.getFile().toFile());
        assertEquals("Loan Report/1", json.get("reportCode").asText());
        assertEquals(42, json.get("executionId").asInt());
        assertEquals("2026-01-01", json.get("parameters").get("from").asText());
        assertEquals(2, json.get("rows").size());
        assertTrue(json.get("rows").get(1).get("NAME").isNull());
        assertEquals("application/json", result.getContentType().split(";")[0]);
    }

    @Test
    void closeIsIdempotent() {
        StreamingCsvReportBatchWriter writer = new StreamingCsvReportBatchWriter(
                new ReportOutputFileFactory(storage.toString()), new ReportTemplateColumnResolver(objectMapper));
        ReportBatchContext ctx = context(ReportFormat.CSV);
        ReportFormatResult first = run(writer, ctx, List.of(row(1, "a")));
        writer.close(ctx);
        assertSame(first, ctx.getSharedState().get(ReportBatchSharedStateKeys.STREAMING_RESULT));
    }
}
