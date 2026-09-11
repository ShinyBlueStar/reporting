package com.sample.system.reporting.service.batch;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ReportBatchSharedStateRegistry {

    private final ConcurrentHashMap<Long, ConcurrentMap<String, Object>> states = new ConcurrentHashMap<>();

    public void bind(Long reportExecutionId, ConcurrentMap<String, Object> sharedState) {
        states.put(reportExecutionId, sharedState);
    }

    public ConcurrentMap<String, Object> release(Long reportExecutionId) {
        return states.remove(reportExecutionId);
    }

    public ConcurrentMap<String, Object> get(Long reportExecutionId) {
        return states.get(reportExecutionId);
    }
}
