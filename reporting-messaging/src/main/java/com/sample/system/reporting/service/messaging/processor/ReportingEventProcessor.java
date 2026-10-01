package com.sample.system.reporting.service.messaging.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.reporting.service.application.ports.input.ReportingSyncService;
import com.sample.system.reporting.service.application.event.model.EventEnvelope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReportingEventProcessor {

    private final ObjectMapper objectMapper;
    private final ReportingSyncService reportingSyncService;

    public ReportingEventProcessor(ObjectMapper objectMapper, ReportingSyncService reportingSyncService) {
        this.objectMapper = objectMapper;
        this.reportingSyncService = reportingSyncService;
    }

    public void process(String messageBody) {
        try {
            EventEnvelope eventEnvelope = objectMapper.readValue(messageBody, EventEnvelope.class);
            reportingSyncService.sync(eventEnvelope);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Invalid reporting event envelope", ex);
        }
    }
}
