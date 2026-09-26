package com.sample.system.reporting.service.application.ports.input;

import com.sample.system.reporting.service.domain.model.entity.PartyReport;

public interface PartyReportProjectionService {

    void sync(PartyReport partyReport);
}
