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
@Table(name = "report_schedule")
public class ReportScheduleEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_schedule_seq")
    @SequenceGenerator(name = "report_schedule_seq", sequenceName = "REPORT_SCHEDULE_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_definition_id")
    private ReportDefinitionEntity reportDefinition;

    @Column(name = "schedule_name", length = 200, nullable = false)
    private String scheduleName;

    @Column(name = "cron_expression", length = 100, nullable = false)
    private String cronExpression;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "output_type", length = 50)
    private String outputType;

    @Column(name = "last_execution_time")
    private Instant lastExecutionTime;

    @Column(name = "next_execution_time")
    private Instant nextExecutionTime;

    @Column(name = "notify_email", length = 500)
    private String notifyEmail;

    @Column(name = "schedule_parameters")
    @Lob
    private String scheduleParameters;
    /*{
  "fromDate":"2025-01-01T00:00:00Z",
  "toDate":"2025-12-31T23:59:59Z"
}*/

    @Column(name = "last_status")
    private String lastStatus; // success-failed
}
