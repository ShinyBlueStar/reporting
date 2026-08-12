package com.sample.system.reporting.service.application.validation.model;

import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public record ValidationContext(
        ReportDefinition reportDefinition,
        Map<String, Object> parameters,
        String username,
        Locale locale) {

    public ValidationContext {
        parameters = parameters == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
        locale = locale == null ? Locale.getDefault() : locale;
    }
}
