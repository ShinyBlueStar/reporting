package com.sample.system.reporting.service.batch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
final class JsonStreamingReportResource extends StreamingReportResource {

    private final JsonGenerator generator;
    private final ObjectMapper objectMapper;
    private long rowsWritten;

    JsonStreamingReportResource(ReportOutputFile outputFile,
                                JsonGenerator generator,
                                ObjectMapper objectMapper) {
        super(outputFile);
        this.generator = generator;
        this.objectMapper = objectMapper;
    }

    void start(ReportBatchContext context) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("reportCode", context.getReportDefinition().getReportCode());
        generator.writeFieldName("executionId");
        if (context.getReportExecution().getId() != null) {
            generator.writeNumber(context.getReportExecution().getId().getValue());
        } else {
            generator.writeNull();
        }
        generator.writeFieldName("parameters");
        objectMapper.writeValue(generator, context.getParameters());
        generator.writeFieldName("rows");
        generator.writeStartArray();
        log.info("[BatchWriter] JSON stream started. executionId={}, reportCode={}, fileName={}, parameterCount={}",
                context.getReportExecution().getId() != null
                        ? context.getReportExecution().getId().getValue()
                        : null,
                context.getReportDefinition().getReportCode(),
                getOutputFile().getFileName(),
                context.getParameters() != null ? context.getParameters().size() : 0);
    }

    void writeRows(List<? extends Map<String, Object>> rows) throws IOException {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (Map<String, Object> row : rows) {
            objectMapper.writeValue(generator, row);
            rowsWritten++;
            if (rowsWritten == 1) {
                log.info("[BatchWriter] JSON first row written. fileName={}, rowKeys={}",
                        getOutputFile().getFileName(),
                        row.keySet());
            }
        }
        generator.flush();
    }

    @Override
    protected void finishContent() throws IOException {
        generator.writeEndArray();
        generator.writeEndObject();
        generator.flush();
        generator.close();
        log.info("[BatchWriter] JSON content finalized. fileName={}, rowsWritten={}",
                getOutputFile().getFileName(),
                rowsWritten);
    }
}
