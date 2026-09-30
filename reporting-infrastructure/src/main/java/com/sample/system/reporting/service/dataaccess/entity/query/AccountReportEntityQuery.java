package com.sample.system.reporting.service.dataaccess.entity.query;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "report_account",
        indexes = @Index(
                name = "idx_report_account_number",
                columnList = "account_number"
        )
)
public class AccountReportEntityQuery extends BaseReportEntityQuery {

    @Column(name = "party_id")
    private Long partyId;

    @Column(name = "national_code", length = 20)
    private String nationalCode;

    @Column(name = "party_name", length = 200)
    private String partyName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "account_type", length = 50)
    private String accountType;

    @Column(name = "balance", precision = 18, scale = 2)
    private BigDecimal balance;

    /**
     * Original account creation time
     */
    @Column(name = "account_created_at")
    private Instant accountCreatedAt;

    /**
     * Original account update time
     */
    @Column(name = "account_updated_at")
    private Instant accountUpdatedAt;

    @Column(name = "status", length = 50)
    private String status;

    @Lob
    @Column(name = "payload")
    private String payload;
}