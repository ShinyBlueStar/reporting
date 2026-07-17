package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ReportDefinitionRepository extends JpaRepository<ReportDefinitionEntity, Long>,
        JpaSpecificationExecutor<ReportDefinitionEntity> {

    Optional<ReportDefinitionEntity> findByReportCode(String reportCode);

    Optional<ReportDefinitionEntity> findByReportName(String reportName);

    List<ReportDefinitionEntity> findByActiveTrue();
}
