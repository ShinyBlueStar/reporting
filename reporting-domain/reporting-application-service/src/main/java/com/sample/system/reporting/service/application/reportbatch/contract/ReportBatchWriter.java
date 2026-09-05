package com.sample.system.reporting.service.application.reportbatch.contract;


import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import com.sample.system.reporting.service.domain.model.enums.ReportFormat;

import java.util.List;

public interface ReportBatchWriter<O> {

    boolean supports(ReportDefinition reportDefinition, ReportFormat reportFormat);

    default void open(ReportBatchContext context) {
    }

    void write(List<? extends O> items, ReportBatchContext context);

    default void close(ReportBatchContext context) {
    }
}
