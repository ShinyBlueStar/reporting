package com.sample.system.reporting.service.application.ports.output;

import java.util.Map;

public interface ValidationQueryPort {

    long executeCount(String sql, Map<String, Object> parameters);
}
