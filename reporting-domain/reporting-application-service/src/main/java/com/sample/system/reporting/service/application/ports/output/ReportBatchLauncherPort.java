package com.sample.system.reporting.service.application.ports.output;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchLaunchRequest;

import java.util.concurrent.ConcurrentMap;

public interface ReportBatchLauncherPort {

    ConcurrentMap<String, Object> launch(ReportBatchLaunchRequest request);
}
