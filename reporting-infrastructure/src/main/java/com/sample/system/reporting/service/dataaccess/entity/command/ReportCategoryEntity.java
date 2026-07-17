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
        name = "report_category",
        uniqueConstraints = @UniqueConstraint(name = "uk_report_category_name", columnNames = "category_name")
)
public class ReportCategoryEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_category_seq")
    @SequenceGenerator(name = "report_category_seq", sequenceName = "REPORT_CATEGORY_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "category_name", length = 100, nullable = false)
    private String categoryName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active")
    private Boolean active;
}