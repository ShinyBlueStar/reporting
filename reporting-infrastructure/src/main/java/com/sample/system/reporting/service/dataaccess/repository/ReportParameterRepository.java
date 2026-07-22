package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportParameterRepository extends JpaRepository<ReportParameterEntity, Long> {

    List<ReportParameterEntity> findByReportDefinitionIdOrderByDisplayOrderAsc(Long reportDefinitionId);

    Optional<ReportParameterEntity> findByReportDefinitionIdAndParameterName(
            Long reportDefinitionId, String parameterName);
}
