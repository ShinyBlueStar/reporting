package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.query.AccountReportEntityQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountReportRepository extends JpaRepository<AccountReportEntityQuery, Long> {

    Optional<AccountReportEntityQuery> findByAggregateId(String aggregateId);
}
