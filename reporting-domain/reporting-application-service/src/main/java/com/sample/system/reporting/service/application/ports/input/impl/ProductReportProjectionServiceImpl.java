package com.sample.system.reporting.service.application.ports.input.impl;

import com.sample.system.reporting.service.application.ports.input.ProductReportProjectionService;
import com.sample.system.reporting.service.application.ports.output.IReportProjectionRepository;
import com.sample.system.reporting.service.domain.model.entity.ProductReport;
import org.springframework.stereotype.Service;

@Service
public class ProductReportProjectionServiceImpl implements ProductReportProjectionService {

    private final IReportProjectionRepository reportProjectionPort;

    public ProductReportProjectionServiceImpl(IReportProjectionRepository reportProjectionPort) {
        this.reportProjectionPort = reportProjectionPort;
    }

    @Override
    public void sync(ProductReport productReport) {
        reportProjectionPort.upsertProduct(productReport);
    }
}
