package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchProcessor;
import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchReader;
import com.sample.system.reporting.service.application.reportbatch.contract.ReportBatchWriter;
import com.sample.system.reporting.service.domain.exception.ReportingDomainException;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportBatchComponentRegistryTest {

    @SuppressWarnings("unchecked")
    private final ReportBatchReader<Object> reader = mock(ReportBatchReader.class);
    @SuppressWarnings("unchecked")
    private final ReportBatchProcessor<Object, Object> processor = mock(ReportBatchProcessor.class);
    @SuppressWarnings("unchecked")
    private final ReportBatchWriter<Object> csvWriter = mock(ReportBatchWriter.class);
    @SuppressWarnings("unchecked")
    private final ReportBatchWriter<Object> jsonWriter = mock(ReportBatchWriter.class);
    private final ReportDefinition definition = new ReportDefinition();

    private ReportBatchComponentRegistry registry() {
        return new ReportBatchComponentRegistry(List.of(reader), List.of(processor), List.of(csvWriter, jsonWriter));
    }

    @Test
    void selectsFirstSupportingComponentAndWriterByFormat() {
        when(reader.supports(definition)).thenReturn(true);
        when(processor.supports(definition)).thenReturn(true);
        when(csvWriter.supports(definition, ReportFormat.CSV)).thenReturn(true);
        when(jsonWriter.supports(definition, ReportFormat.JSON)).thenReturn(true);

        assertSame(reader, registry().readerFor(definition));
        assertSame(processor, registry().processorFor(definition));
        assertSame(csvWriter, registry().writerFor(definition, ReportFormat.CSV));
        assertSame(jsonWriter, registry().writerFor(definition, ReportFormat.JSON));
    }

    @Test
    void missingComponentFailsWithDomainException() {
        assertThrows(ReportingDomainException.class, () -> registry().readerFor(definition));
        assertThrows(ReportingDomainException.class, () -> registry().writerFor(definition, ReportFormat.XLSX));
    }
}
