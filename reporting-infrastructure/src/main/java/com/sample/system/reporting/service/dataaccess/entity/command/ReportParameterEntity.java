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
@Table(name = "report_parameter")
public class ReportParameterEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_parameter_seq")
    @SequenceGenerator(name = "report_parameter_seq", sequenceName = "REPORT_PARAMETER_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_definition_id")
    private ReportDefinitionEntity reportDefinition;

    @Column(name = "parameter_name", length = 100, nullable = false)
    private String parameterName;

    @Column(name = "parameter_label", length = 200)
    private String parameterLabel;

    @Column(name = "parameter_type", length = 50)
    private String parameterType;

    @Column(name = "required")
    private Boolean required;

    @Column(name = "default_value", length = 500)
    private String defaultValue;

    @Column(name = "validation_regex", length = 500)
    private String validationRegex;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "placeholder")
    private String placeholder;

    @Column(name = "help_text", length = 1000)
    private String helpText;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "min_length")
    private Integer minLength;
}
