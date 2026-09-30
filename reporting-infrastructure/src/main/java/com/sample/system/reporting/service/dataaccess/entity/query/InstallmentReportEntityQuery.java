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
        name = "report_installment",
        indexes = {
                @Index(name = "idx_report_installment_loan", columnList = "loan_id"),
                @Index(name = "idx_report_installment_loan_file", columnList = "loan_file_no"),
                @Index(name = "idx_report_installment_due_date", columnList = "due_date"),
                @Index(name = "idx_report_installment_status", columnList = "installment_status")
        }
)
public class InstallmentReportEntityQuery extends BaseReportEntityQuery {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanReportEntityQuery loan;

    @Column(name = "source_installment_id")
    private Long sourceInstallmentId;

    @Column(name = "loan_file_no", length = 50)
    private String loanFileNo;

    @Column(name = "installment_no")
    private Integer installmentNo;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "installment_amount", precision = 18, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "total_paid", precision = 18, scale = 2)
    private BigDecimal totalPaid;

    @Column(name = "installment_status", length = 50)
    private String installmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "installment_status",
            referencedColumnName = "status_code",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private InstallmentStatusLookupEntity installmentStatusLookup;

    @Column(name = "last_payment_date")
    private Instant lastPaymentDate;

    @Column(name = "remaining_total", precision = 18, scale = 2)
    private BigDecimal remainingTotal;

    @Column(name = "remaining_principal", precision = 18, scale = 2)
    private BigDecimal remainingPrincipal;

    @Column(name = "remaining_interest", precision = 18, scale = 2)
    private BigDecimal remainingInterest;

    @Column(name = "remaining_penalty", precision = 18, scale = 2)
    private BigDecimal remainingPenalty;

    @Column(name = "remaining_post_maturity_interest", precision = 18, scale = 2)
    private BigDecimal remainingPostMaturityInterest;

    @Lob
    @Column(name = "payload")
    private String payload;
}
