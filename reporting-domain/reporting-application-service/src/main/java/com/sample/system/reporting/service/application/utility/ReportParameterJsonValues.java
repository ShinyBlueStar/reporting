package com.sample.system.reporting.service.application.utility;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ReportParameterJsonValues {

    private ReportParameterJsonValues() {
    }

    public static Map<String, Object> toMap(JsonNode root) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        if (root == null || root.isNull()) {
            return parameters;
        }
        root.fields().forEachRemaining(entry -> parameters.put(entry.getKey(), fromNode(entry.getValue())));
        return parameters;
    }

    public static Object fromNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return ReportInstantParser.parseTextValueOrKeep(node.asText().trim());
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isNumber()) {
            return node.decimalValue();
        }
        return node.toString();
    }
}
