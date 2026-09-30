package com.sample.system.reporting.service.dataaccess.entity.query;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "loan_status_lookup")
public class LoanStatusLookupEntity {

    @Id
    @Column(name = "status_code", length = 50, nullable = false)
    private String statusCode;

    @Column(name = "status_title_fa", length = 200, nullable = false)
    private String statusTitleFa;

    @Column(name = "status_title_en", length = 200, nullable = false)
    private String statusTitleEn;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean active;
}
