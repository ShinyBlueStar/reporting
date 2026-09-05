package com.sample.system.reporting.service.dataaccess.entity.command;

import com.sample.system.reporting.service.dataaccess.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.processing.SQL;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.time.Instant;

@Audited
@AuditOverride(forClass = Auditable.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_execution_detail")
public class ReportExecutionDetailEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_execution_detail_seq")
    @SequenceGenerator(name = "report_execution_detail_seq", sequenceName = "REPORT_EXECUTION_DETAIL_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "report_execution_id", nullable = false)
    private Long reportExecutionId;

    @Column(name = "step_name", length = 200, nullable = false)
    private String stepName;

    @Column(name = "step_status", length = 50)
    private String stepStatus;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "message", length = 2000)
    private String message;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "step_order")
    private Integer stepOrder; //1 Validation - 2 SQL -3 Export -4 Upload
}
