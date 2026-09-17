package com.sample.system.reporting.service.batch;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import com.sample.system.reporting.service.reportformat.ReportFormatColumn;
import com.sample.system.reporting.service.reportformat.ReportTemplateColumnResolver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
final class XlsxStreamingReportResource extends StreamingReportResource
        implements ReportTemplateAwareStreamingResource {

    private final ReportTemplateColumnsState columnsState;
    private ExcelWriter excelWriter;
    private WriteSheet writeSheet;
    private OutputStream outputStream;
    private long rowsWritten;
    private boolean firstRowLogged;
    private boolean finished;

    XlsxStreamingReportResource(ReportOutputFile outputFile,
                                ReportTemplateColumnResolver columnResolver,
                                ReportTemplate reportTemplate) {
        super(outputFile);
        this.columnsState = new ReportTemplateColumnsState(columnResolver, reportTemplate);
        log.info("[BatchWriter] XLSX stream ready. fileName={}, templatePresent={}, initialColumnCount={}",
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
            log.info("[BatchWriter] XLSX template applied. fileName={}, columnCount={}",
                    getOutputFile().getFileName(),
                    resolvedCount);
        }
    }

    void writeRows(List<? extends Map<String, Object>> rows) throws IOException {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        ensureWriter(rows.get(0));
        List<List<String>> batch = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            batch.add(rowValues(row));
            rowsWritten++;
            if (!firstRowLogged) {
                firstRowLogged = true;
                logFirstRowMapping(row);
            }
        }
        excelWriter.write(batch, writeSheet);
    }

    private void logFirstRowMapping(Map<String, Object> row) {
        List<ReportFormatColumn> columns = columnsState.columns();
        long matchedColumns = columns.stream()
                .filter(column -> ReportRowFieldLookup.value(row, column.getField()) != null)
                .count();
        log.info("[BatchWriter] XLSX column mapping. fileName={}, templateColumns={}, rowKeys={}, matchedNonNullColumns={}",
                getOutputFile().getFileName(),
                columns.stream().map(ReportFormatColumn::getField).toList(),
                row.keySet(),
                matchedColumns);
        if (matchedColumns == 0) {
            log.warn("[BatchWriter] No template field matched row keys. Excel cells will be empty. "
                    + "Align report_template.columns_json field values with SQL column aliases.");
        }
    }

    private void ensureWriter(Map<String, Object> sampleRow) throws IOException {
        if (excelWriter != null) {
            return;
        }
        resolveColumns(sampleRow);
        List<ReportFormatColumn> columns = columnsState.columns();
        List<List<String>> head = columns.stream()
                .map(column -> List.of(ReportExcelCellValueConverter.sanitize(column.titleOrField())))
                .toList();
        Files.createDirectories(getOutputFile().getFinalFile().getParent());
        outputStream = Files.newOutputStream(
                getOutputFile().getFinalFile(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        excelWriter = EasyExcel.write(outputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .autoCloseStream(false)
                .head(head)
                .build();
        writeSheet = EasyExcel.writerSheet(sheetName())
                .registerWriteHandler(new RightToLeftSheetWriteHandler())
                .build();
        log.info("[BatchWriter] XLSX writer opened. fileName={}, sheetName={}, columnCount={}, outputPath={}",
                getOutputFile().getFileName(),
                sheetName(),
                columns.size(),
                getOutputFile().getFinalFile());
    }

    private void resolveColumns(Map<String, Object> row) {
        columnsState.resolveFromRow(row);
        if (columnsState.columns().isEmpty()) {
            ReportFormatColumn fallback = new ReportFormatColumn();
            fallback.setField("value");
            fallback.setTitle("value");
            columnsState.setColumns(List.of(fallback));
        }
    }

    private List<String> rowValues(Map<String, Object> row) {
        return columnsState.columns().stream()
                .map(column -> ReportExcelCellValueConverter.asString(
                        ReportRowFieldLookup.value(row, column.getField())))
                .toList();
    }

    private List<String> emptyRowValues() {
        return columnsState.columns().stream().map(column -> "").toList();
    }

    private String sheetName() {
        ReportTemplate reportTemplate = columnsState.reportTemplate();
        if (reportTemplate != null
                && reportTemplate.getSheetName() != null
                && !reportTemplate.getSheetName().isBlank()) {
            return ReportExcelCellValueConverter.sanitizeSheetName(reportTemplate.getSheetName());
        }
        return "Report";
    }

    @Override
    protected void finishContent() throws IOException {
        if (finished) {
            return;
        }
        try {
            if (excelWriter == null) {
                ensureWriter(Map.of());
            }
            if (rowsWritten == 0) {
                log.info("[BatchWriter] No data rows returned; writing empty row to keep XLSX valid. fileName={}, columnCount={}",
                        getOutputFile().getFileName(),
                        columnsState.columns().size());
                excelWriter.write(Collections.singletonList(emptyRowValues()), writeSheet);
            }
            excelWriter.finish();
        } finally {
            excelWriter = null;
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            finished = true;
        }
        log.info("[BatchWriter] XLSX content finalized. fileName={}, rowsWritten={}, columnCount={}",
                getOutputFile().getFileName(),
                rowsWritten,
                columnsState.columns() != null ? columnsState.columns().size() : 0);
    }
}
