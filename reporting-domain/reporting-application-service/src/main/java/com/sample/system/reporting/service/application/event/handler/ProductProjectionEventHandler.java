package com.sample.system.reporting.service.application.event.handler;

import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import com.sample.system.reporting.service.application.mapper.ReportingProjectionMapper;
import com.sample.system.reporting.service.application.ports.input.ProductReportProjectionService;
import org.springframework.stereotype.Component;

@Component
public class ProductProjectionEventHandler extends AbstractProjectionEventHandler {

    private final ProductReportProjectionService productReportProjectionService;
    private final ReportingProjectionMapper reportingProjectionMapper;

    public ProductProjectionEventHandler(ProductReportProjectionService productReportProjectionService,
                                         ReportingProjectionMapper reportingProjectionMapper) {
        super("CONTRACT", "PRODUCT");
        this.productReportProjectionService = productReportProjectionService;
        this.reportingProjectionMapper = reportingProjectionMapper;
    }

    @Override
    public void handle(EventEnvelope eventEnvelope) {
        productReportProjectionService.sync(
                reportingProjectionMapper.eventEnvelopeToProductReport(eventEnvelope));
    }
}
