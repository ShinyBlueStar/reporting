package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.CardReport;

public interface CardReportProjectionService {

    void sync(CardReport cardReport);
}
