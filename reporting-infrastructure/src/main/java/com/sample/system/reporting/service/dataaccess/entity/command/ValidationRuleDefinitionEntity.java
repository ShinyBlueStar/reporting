package com.sample.system.reporting.service.dataaccess.entity.command;

import com.sample.system.reporting.service.dataaccess.entity.Auditable;
import com.sample.system.reporting.service.domain.model.enums.ValidationRuleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@Audited
@AuditOverride(forClass = Auditable.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_validation_rule")
public class ValidationRuleDefinitionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_validation_rule_seq")
    @SequenceGenerator(
            name = "report_validation_rule_seq",
            sequenceName = "REPORT_VALIDATION_RULE_SEQ",
            allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_definition_id", nullable = false)
    private ReportDefinitionEntity reportDefinition;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", length = 50, nullable = false)
    private ValidationRuleType validationRuleType;

    @Lob
    @Column(name = "configuration")
    private String configuration;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "execution_order")
    private Integer executionOrder;

    @Column(name = "enabled")
    private Boolean enabled;
}
