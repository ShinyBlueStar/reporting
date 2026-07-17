package com.sample.system.reporting.service.dataaccess.entity.command;

import com.sample.system.reporting.service.dataaccess.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Audited
@AuditOverride(forClass = Auditable.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "report_definition",
        uniqueConstraints = @UniqueConstraint(name = "uk_report_definition_code", columnNames = "report_code")
)
public class ReportDefinitionEntity extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_definition_seq")
    @SequenceGenerator(name = "report_definition_seq", sequenceName = "REPORT_DEFINITION_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "report_code", length = 100, unique = true, nullable = false)
    private String reportCode;

    @Column(name = "report_name", length = 200, unique = true, nullable = false)
    private String reportName;

    @Column(name = "report_description", length = 1000)
    private String reportDescription;

    @Column(name = "category", length = 100)
    private String category;

    @Lob
    @Column(name = "sql_query")
    private String sqlQuery;

    @Column(name = "output_type", length = 50)
    private String outputType;

    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "version")
    private Integer version;

    @Column(name = "max_export_rows")
    private Long maxExportRows;
}
