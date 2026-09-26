package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.ProductReport;

public interface ProductReportProjectionService {

    void sync(ProductReport productReport);
}
