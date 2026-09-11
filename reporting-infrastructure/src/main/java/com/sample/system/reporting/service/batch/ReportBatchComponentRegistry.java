package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchProcessor;
import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchReader;
import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchWriter;
import com.sample.system.reporting.service.domain.exception.ErrorCode;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ReportBatchComponentRegistry {

    private final List<ReportBatchReader<?>> readers;
    private final List<ReportBatchProcessor<?, ?>> processors;
    private final List<ReportBatchWriter<?>> writers;

    public ReportBatchComponentRegistry(List<ReportBatchReader<?>> readers,
                                        List<ReportBatchProcessor<?, ?>> processors,
                                        List<ReportBatchWriter<?>> writers) {
        this.readers = readers;
        this.processors = processors;
        this.writers = writers;
    }

    @SuppressWarnings("unchecked")
    public <I> ReportBatchReader<I> readerFor(ReportDefinition definition) {
        ReportBatchReader<I> reader = (ReportBatchReader<I>) readers.stream()
                .filter(candidate -> candidate.supports(definition))
                .findFirst()
                .orElseThrow(() -> missingComponent("reader", definition.getReportCode()));
        log.info("[BatchRegistry] Selected reader. reportCode={}, reader={}",
                definition.getReportCode(),
                reader.getClass().getSimpleName());
        return reader;
    }

    @SuppressWarnings("unchecked")
    public <I, O> ReportBatchProcessor<I, O> processorFor(ReportDefinition definition) {
        ReportBatchProcessor<I, O> processor = (ReportBatchProcessor<I, O>) processors.stream()
                .filter(candidate -> candidate.supports(definition))
                .findFirst()
                .orElseThrow(() -> missingComponent("processor", definition.getReportCode()));
        log.info("[BatchRegistry] Selected processor. reportCode={}, processor={}",
                definition.getReportCode(),
                processor.getClass().getSimpleName());
        return processor;
    }

    @SuppressWarnings("unchecked")
    public <O> ReportBatchWriter<O> writerFor(ReportDefinition definition, ReportFormat format) {
        ReportBatchWriter<O> writer = (ReportBatchWriter<O>) writers.stream()
                .filter(candidate -> candidate.supports(definition, format))
                .findFirst()
                .orElseThrow(() -> missingComponent("writer", definition.getReportCode() + "/" + format.name()));
        log.info("[BatchRegistry] Selected writer. reportCode={}, format={}, writer={}",
                definition.getReportCode(),
                format,
                writer.getClass().getSimpleName());
        return writer;
    }

    private ReportingDomainException missingComponent(String component, String reportCode) {
        return new ReportingDomainException(
                ErrorCode.REPORT_EXECUTION_FAILED.getCode(),
                "No report batch " + component + " found for report: " + reportCode);
    }
}
