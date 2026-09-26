package com.sample.system.reporting.service.domain.model.entity;

import com.sample.system.reporting.service.domain.model.valueObject.ProductReportId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReport extends AggregateRoot<ProductReportId> implements ProjectionReport {

    private String aggregateId;
    private Long aggregateVersion;
    private String lastEventId;
    private String sourceService;
    private String payload;
    private Long productCode;
    private String productName;
    private String nationalId;
    private String status;
    private Instant reportDate;
}
