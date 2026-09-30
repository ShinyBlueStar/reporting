package com.sample.system.reporting.service.dataaccess.entity.query;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "report_product")
public class ProductReportEntityQuery extends BaseReportEntityQuery {

    @Column(name = "product_code")
    private Long productCode;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "national_id", length = 20)
    private String nationalId;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "report_date")
    private Instant reportDate;

    @Lob
    @Column(name = "payload")
    private String payload;
}
