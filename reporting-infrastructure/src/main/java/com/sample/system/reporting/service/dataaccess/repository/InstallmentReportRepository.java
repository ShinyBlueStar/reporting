package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.query.InstallmentReportEntityQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstallmentReportRepository extends JpaRepository<InstallmentReportEntityQuery, Long> {

    Optional<InstallmentReportEntityQuery> findByAggregateId(String aggregateId);

    List<InstallmentReportEntityQuery> findByLoan_Id(Long loanId);
}
