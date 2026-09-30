package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.query.ProductReportEntityQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReportRepository extends JpaRepository<ProductReportEntityQuery, Long> {

    Optional<ProductReportEntityQuery> findByAggregateId(String aggregateId);
}
