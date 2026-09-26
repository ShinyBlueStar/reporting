package com.sample.system.reporting.service.application.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public final class ProjectionPayloadSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ProjectionPayloadSerializer() {
    }

    public static String toJson(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Cannot serialize projection payload", ex);
        }
    }
}
