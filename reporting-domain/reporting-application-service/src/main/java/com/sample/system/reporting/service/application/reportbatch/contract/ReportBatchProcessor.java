package com.sample.system.reporting.service.application.reportbatch.contract;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;

public interface ReportBatchProcessor<I, O> {

    boolean supports(ReportDefinition reportDefinition);

    O process(I item, ReportBatchContext context);
}
