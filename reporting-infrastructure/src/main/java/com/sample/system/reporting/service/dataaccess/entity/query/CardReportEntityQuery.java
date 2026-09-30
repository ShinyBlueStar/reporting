package com.sample.system.reporting.service.dataaccess.entity.query;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "report_card")
public class CardReportEntityQuery extends BaseReportEntityQuery {

    @Column(name = "national_code", length = 20)
    private String nationalCode;

    @Column(name = "card_number", length = 30)
    private String cardNumber;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "card_status", length = 50)
    private String cardStatus;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "report_date")
    private Instant reportDate;

    @Lob
    @Column(name = "payload")
    private String payload;
}
