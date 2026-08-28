package com.sample.system.reporting.service.application.ports.output;

import java.util.Map;
import java.util.function.Consumer;

public interface ReportQueryExecutorPort {

    void executeQuery(String sql,
                      Map<String, Object> parameters,
                      Integer timeoutSeconds,
                      int maxRows,
                      Consumer<Map<String, Object>> consumer);
}
