package com.sample.system.reporting.service.dataaccess.entity.command;

import com.sample.system.reporting.service.dataaccess.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import java.time.Instant;

@Audited
@AuditOverride(forClass = Auditable.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_execution")
public class ReportExecutionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_execution_seq")
    @SequenceGenerator(name = "report_execution_seq", sequenceName = "REPORT_EXECUTION_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_definition_id")
    private ReportDefinitionEntity reportDefinition;

    @Column(name = "execution_status", length = 50)
    private String executionStatus;

    @Column(name = "execution_start_time")
    private Instant executionStartTime;

    @Column(name = "execution_end_time")
    private Instant executionEndTime;

    @Column(name = "execution_duration_ms")
    private Long executionDurationMs;

    @Column(name = "total_record_count")
    private Long totalRecordCount;

    @Column(name = "requested_by", length = 100)
    private String requestedBy;

    @Lob
    @Column(name = "request_parameters")
    private String requestParameters;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_file_id")
    private ReportFileEntity generatedFile;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "execution_source")
    private String executionSource; // enum MANUAL -SCHEDULED -API

    @Column(name = "correlation_id", length = 100)
    private String correlationId;
}
