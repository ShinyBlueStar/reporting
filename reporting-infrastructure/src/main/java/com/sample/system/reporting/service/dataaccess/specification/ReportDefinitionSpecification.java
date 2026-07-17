package com.sample.system.reporting.service.dataaccess.specification;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.domain.model.ReportDefinitionSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ReportDefinitionSpecification {

    private ReportDefinitionSpecification() {
    }

    public static Specification<ReportDefinitionEntity> from(ReportDefinitionSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getReportCode() != null && !criteria.getReportCode().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("reportCode")),
                        "%" + criteria.getReportCode().toLowerCase() + "%"));
            }
            if (criteria.getReportName() != null && !criteria.getReportName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("reportName")),
                        "%" + criteria.getReportName().toLowerCase() + "%"));
            }
            if (criteria.getCategoryName() != null && !criteria.getCategoryName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("category")),
                        "%" + criteria.getCategoryName().toLowerCase() + "%"));
            }
            if (criteria.getReportType() != null && !criteria.getReportType().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("reportType"), criteria.getReportType()));
            }
            if (criteria.getActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), criteria.getActive()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
