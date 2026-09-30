package com.sample.system.reporting.service.dataaccess.entity.query;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "report_loan",
        indexes = {
                @Index(name = "idx_report_loan_party", columnList = "party_id"),
                @Index(name = "idx_report_loan_file_no", columnList = "file_no"),
                @Index(name = "idx_report_loan_loan_file_no", columnList = "loan_file_no"),
                @Index(name = "idx_report_loan_national_code", columnList = "national_code"),
                @Index(name = "idx_report_loan_unit_code", columnList = "unit_code"),
                @Index(name = "idx_report_loan_creation_date", columnList = "creation_date")
        }
)
public class LoanReportEntityQuery extends BaseReportEntityQuery {

    @OneToMany(mappedBy = "loan", fetch = FetchType.LAZY)
    private List<InstallmentReportEntityQuery> installments = new ArrayList<>();

    @Column(name = "source_loan_id")
    private Long sourceLoanId;

    @Column(name = "party_id")
    private Long partyId;

    @Column(name = "national_code", length = 20)
    private String nationalCode;

    @Column(name = "national_name", length = 200)
    private String nationalName;

    @Column(name = "file_no", length = 50)
    private String fileNo;

    @Column(name = "loan_file_no", length = 50)
    private String loanFileNo;

    @Column(name = "loan_profile_name", length = 200)
    private String loanProfileName;

    @Column(name = "unit_code", length = 20)
    private String unitCode;

    @Column(name = "unit_name", length = 200)
    private String unitName;

    @Column(name = "creation_date")
    private Instant creationDate;

    @Column(name = "requested_amount", precision = 18, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "total_interest", precision = 18, scale = 2)
    private BigDecimal totalInterest;

    @Column(name = "installment_count")
    private Integer installmentCount;

    @Column(name = "paid_installment_count")
    private Integer paidInstallmentCount;

    @Column(name = "first_installment_date")
    private Instant firstInstallmentDate;

    @Column(name = "last_installment_date")
    private Instant lastInstallmentDate;

    @Column(name = "loan_status", length = 50)
    private String loanStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "loan_status",
            referencedColumnName = "status_code",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private LoanStatusLookupEntity loanStatusLookup;

    @Column(name = "total_remaining_principal", precision = 18, scale = 2)
    private BigDecimal totalRemainingPrincipal;

    @Column(name = "total_remaining_interest", precision = 18, scale = 2)
    private BigDecimal totalRemainingInterest;

    @Column(name = "total_remaining_penalty", precision = 18, scale = 2)
    private BigDecimal totalRemainingPenalty;

    @Column(name = "total_remaining_post_maturity_interest", precision = 18, scale = 2)
    private BigDecimal totalRemainingPostMaturityInterest;

    @Column(name = "total_remaining_before_due_date", precision = 18, scale = 2)
    private BigDecimal totalRemainingBeforeDueDate;

    @Column(name = "total_remaining_past_due_date", precision = 18, scale = 2)
    private BigDecimal totalRemainingPastDueDate;

    @Lob
    @Column(name = "payload")
    private String payload;
}
