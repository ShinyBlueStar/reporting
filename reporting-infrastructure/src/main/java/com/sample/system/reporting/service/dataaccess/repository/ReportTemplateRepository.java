package com.sample.system.reporting.service.dataaccess.repository;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplateEntity, Long> {

    Optional<ReportTemplateEntity> findFirstByReportDefinition_IdOrderByIdAsc(Long reportDefinitionId);

    Optional<ReportTemplateEntity> findByReportDefinition_IdAndTemplateName(Long reportDefinitionId, String templateName);

    List<ReportTemplateEntity> findByReportDefinition_IdOrderByIdAsc(Long reportDefinitionId);
}
