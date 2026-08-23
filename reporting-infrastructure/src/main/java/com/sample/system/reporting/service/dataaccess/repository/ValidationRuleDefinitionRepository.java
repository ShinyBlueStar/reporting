package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.command.ValidationRuleDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValidationRuleDefinitionRepository extends JpaRepository<ValidationRuleDefinitionEntity, Long> {

    List<ValidationRuleDefinitionEntity> findByReportDefinition_IdOrderByExecutionOrderAsc(Long reportDefinitionId);
}
