package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.domain.model.entity.ReportTemplate;

interface ReportTemplateAwareStreamingResource {

    void applyTemplate(ReportTemplate reportTemplate);
}
