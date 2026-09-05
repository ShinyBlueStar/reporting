package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.application.ports.output.IReportFileRepository;
import com.sample.system.reporting.service.dataaccess.mapper.ReportFileDataAccessMapper;
import com.sample.system.reporting.service.dataaccess.repository.ReportFileRepository;
import com.sample.system.reporting.service.domain.model.entity.ReportFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportFilePersistenceAdapter implements IReportFileRepository {

    private final ReportFileRepository reportFileRepository;
    private final ReportFileDataAccessMapper reportFileDataAccessMapper;

    @Override
    public ReportFile findById(Long id) {
        return reportFileRepository.findById(id)
                .map(reportFileDataAccessMapper::reportFileEntityToReportFile)
                .orElse(null);
    }
}
