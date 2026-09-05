package com.sample.system.reporting.service.application.reportbatch.contract;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;

public interface ReportBatchReader<I> {

    boolean supports(ReportDefinition reportDefinition);

    void open(ReportBatchContext context);

    I read();

    default void close() {
    }
}
