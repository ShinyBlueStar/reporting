package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import com.sample.system.reporting.service.reportformat.ReportFormatColumn;
import com.sample.system.reporting.service.reportformat.ReportTemplateColumnResolver;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
final class CsvStreamingReportResource extends StreamingReportResource
        implements ReportTemplateAwareStreamingResource {

    private final BufferedWriter writer;
    private final ReportTemplateColumnsState columnsState;
    private boolean headerWritten;
    private long rowsWritten;
    private boolean firstRowLogged;

    CsvStreamingReportResource(ReportOutputFile outputFile,
                               BufferedWriter writer,
                               ReportTemplateColumnResolver columnResolver,
                               ReportTemplate reportTemplate) {
        super(outputFile);
        this.writer = writer;
        this.columnsState = new ReportTemplateColumnsState(columnResolver, reportTemplate);
        log.info("[BatchWriter] CSV stream ready. fileName={}, templatePresent={}, initialColumnCount={}",
                outputFile.getFileName(),
                reportTemplate != null,
                columnsState.columns() != null ? columnsState.columns().size() : 0);
    }

    @Override
    public void applyTemplate(ReportTemplate reportTemplate) {
        int previousCount = columnsState.columns() != null ? columnsState.columns().size() : 0;
        columnsState.applyTemplate(reportTemplate);
        int resolvedCount = columnsState.columns() != null ? columnsState.columns().size() : 0;
        if (resolvedCount > previousCount) {
            log.info("[BatchWriter] CSV template applied. fileName={}, columnCount={}",
                    getOutputFile().getFileName(),
                    resolvedCount);
        }
    }

    void writeRows(List<? extends Map<String, Object>> rows) throws IOException {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (Map<String, Object> row : rows) {
            columnsState.resolveFromRow(row);
            writeHeaderIfNeeded();
            if (!firstRowLogged) {
                firstRowLogged = true;
                logFirstRowMapping(row);
            }
            writer.write(columnsState.columns().stream()
                    .map(column -> ReportRowFieldLookup.value(row, column.getField()))
                    .map(this::stringValue)
                    .map(this::escape)
                    .collect(Collectors.joining(",")));
            writer.newLine();
            rowsWritten++;
        }
        writer.flush();
    }

    private void logFirstRowMapping(Map<String, Object> row) {
        List<ReportFormatColumn> columns = columnsState.columns();
        long matchedColumns = columns.stream()
                .filter(column -> ReportRowFieldLookup.value(row, column.getField()) != null)
                .count();
        log.info("[BatchWriter] CSV column mapping. fileName={}, templateColumns={}, rowKeys={}, matchedNonNullColumns={}",
                getOutputFile().getFileName(),
                columns.stream().map(ReportFormatColumn::getField).toList(),
                row.keySet(),
                matchedColumns);
        if (matchedColumns == 0) {
            log.warn("[BatchWriter] No template field matched row keys. CSV cells will be empty. "
                    + "Align report_template.columns_json field values with SQL column aliases.");
        }
    }

    private void writeHeaderIfNeeded() throws IOException {
        List<ReportFormatColumn> columns = columnsState.columns();
        if (headerWritten || columns == null || columns.isEmpty()) {
            return;
        }
        writer.write(columns.stream()
                .map(ReportFormatColumn::titleOrField)
                .map(this::escape)
                .collect(Collectors.joining(",")));
        writer.newLine();
        headerWritten = true;
        log.info("[BatchWriter] CSV header written. fileName={}, columnCount={}",
                getOutputFile().getFileName(),
                columns.size());
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString();
    }

    private String escape(String value) {
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    @Override
    protected void finishContent() throws IOException {
        writeHeaderIfNeeded();
        writer.flush();
        writer.close();
        log.info("[BatchWriter] CSV content finalized. fileName={}, rowsWritten={}, columnCount={}",
                getOutputFile().getFileName(),
                rowsWritten,
                columnsState.columns() != null ? columnsState.columns().size() : 0);
    }
}
