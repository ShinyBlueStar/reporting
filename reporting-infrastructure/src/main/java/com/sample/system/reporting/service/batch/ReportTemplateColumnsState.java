package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;
import com.sample.system.reporting.service.reportformat.ReportFormatColumn;
import com.sample.system.reporting.service.reportformat.ReportTemplateColumnResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class ReportTemplateColumnsState {

    private final ReportTemplateColumnResolver columnResolver;
    private ReportTemplate reportTemplate;
    private List<ReportFormatColumn> columns;

    ReportTemplateColumnsState(ReportTemplateColumnResolver columnResolver, ReportTemplate reportTemplate) {
        this.columnResolver = columnResolver;
        this.reportTemplate = reportTemplate;
        this.columns = columnResolver.resolveColumns(reportTemplate, null);
    }

    void applyTemplate(ReportTemplate reportTemplate) {
        if ((columns == null || columns.isEmpty()) && reportTemplate != null) {
            this.reportTemplate = reportTemplate;
            this.columns = columnResolver.resolveColumns(reportTemplate, null);
        }
    }

    void resolveFromRow(Map<String, Object> row) {
        if (columns == null || columns.isEmpty()) {
            columns = new ArrayList<>(columnResolver.resolveColumns(reportTemplate, row));
        }
    }

    ReportTemplate reportTemplate() {
        return reportTemplate;
    }

    List<ReportFormatColumn> columns() {
        return columns;
    }

    void setColumns(List<ReportFormatColumn> columns) {
        this.columns = columns;
    }
}
