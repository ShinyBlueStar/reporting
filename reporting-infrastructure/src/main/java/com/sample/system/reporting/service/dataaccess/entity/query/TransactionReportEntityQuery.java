package com.sample.system.reporting.service.dataaccess.entity.query;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_transaction")
public class TransactionReportEntityQuery extends BaseReportEntityQuery implements Serializable {

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Column(name = "transaction_type", length = 50)
    private String transactionType;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date")
    private Instant transactionDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "report_date")
    private Instant reportDate;

    @Lob
    @Column(name = "payload")
    private String payload;
}
