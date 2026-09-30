package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.query.TransactionReportEntityQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionReportRepository extends JpaRepository<TransactionReportEntityQuery, Long> {

    Optional<TransactionReportEntityQuery> findByAggregateId(String aggregateId);
}
