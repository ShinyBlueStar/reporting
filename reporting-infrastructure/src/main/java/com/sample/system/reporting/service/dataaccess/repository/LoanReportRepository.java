package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.query.LoanReportEntityQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanReportRepository extends JpaRepository<LoanReportEntityQuery, Long> {

    Optional<LoanReportEntityQuery> findByAggregateId(String aggregateId);
}
