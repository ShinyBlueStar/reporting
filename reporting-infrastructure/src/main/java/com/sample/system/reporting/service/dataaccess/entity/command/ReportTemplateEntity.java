package com.sample.system.reporting.service.dataaccess.entity.command;

import com.sample.system.reporting.service.dataaccess.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "report_template",
        uniqueConstraints = @UniqueConstraint(name = "uk_report_template_name", columnNames = {"report_definition_id", "template_name"})
)
public class ReportTemplateEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_template_seq")
    @SequenceGenerator(name = "report_template_seq", sequenceName = "REPORT_TEMPLATE_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_definition_id", nullable = false)
    private ReportDefinitionEntity reportDefinition;

    @Column(name = "template_name", length = 200, nullable = false)
    private String templateName;

    @Column(name = "sheet_name", length = 200, nullable = false)
    private String sheetName;

    @Lob
    @Column(name = "columns_json", nullable = false)
    private String columnsJson;

    /**
     * Example JSON structure:
     * [
     *   { "field": "nationalCode", "title": "National Code", "width": 25 },
     *   { "field": "loanFileNumber", "title": "File Number", "width": 20 }
     * ]
     */
}